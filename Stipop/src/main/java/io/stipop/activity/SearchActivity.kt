package io.stipop.activity

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import io.stipop.*
import io.stipop.adapter.search.KeywordAdapter
import io.stipop.adapter.StickerAdapter
import io.stipop.databinding.ActivitySearchBinding
import io.stipop.extend.RecyclerDecoration
import io.stipop.model.SPSticker
import org.json.JSONObject
import java.io.IOException

class SearchActivity : Activity() {

    lateinit var _binding: ActivitySearchBinding
    lateinit var _context: Context

    lateinit var keywordAdapter: KeywordAdapter
    lateinit var stickerAdapter: StickerAdapter

    var keywords = ArrayList<JSONObject>()
    var stickerData = ArrayList<SPSticker>()

    private var lastItemVisibleFlag = false

    var page = 1
    var totalPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _context = this


        val drawable = _binding.containerLL.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.themeBackgroundColor))

        val drawable2 = _binding.searchbarLL.background as GradientDrawable
        drawable2.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color
        drawable2.cornerRadius = Utils.dpToPx(Config.searchbarRadius.toFloat())

        _binding.searchIV.setImageResource(Config.getSearchbarIconResourceId(_context))
        _binding.eraseIV.setImageResource(Config.getSearchBarDeleteIconResourceId(_context))

        _binding.titleTV.setTextColor(Config.getSearchKeywordTextColor(_context))
        _binding.keywordET.setTextColor(Config.getSearchKeywordTextColor(_context))


        _binding.searchIV.setIconDefaultsColor()
        _binding.eraseIV.setIconDefaultsColor()


        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(Config.themeBackgroundColor), Color.TRANSPARENT)
        )

        _binding.shadowV.background = gd

        _binding.clearTextLL.setOnClickListener {
            _binding.keywordET.setText("")
        }

        _binding.keywordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val keyword = Utils.getString(_binding.keywordET)

                page = 1
                search(keyword)
            }
        })

        keywordAdapter = KeywordAdapter(keywords)
        keywordAdapter.setOnItemClickListener(object : KeywordAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (position > keywords.size) {
                    return
                }

                val item = keywords[position]
                val keyword = Utils.getString(item, "keyword")
                page = 1
                search(keyword)
            }
        })

        stickerAdapter = StickerAdapter(_context, R.layout.item_sticker, stickerData)

        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        _binding.keywordRV.layoutManager = mLayoutManager
        _binding.keywordRV.addItemDecoration(RecyclerDecoration(10))
        _binding.keywordRV.adapter = keywordAdapter

        _binding.stickerGrid.numColumns = Config.searchNumOfColumns
        _binding.stickerGrid.adapter = stickerAdapter
        _binding.stickerGrid.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && totalPage > page) {
                    page += 1

                    search(Utils.getString(_binding.keywordET))
                }
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                lastItemVisibleFlag =
                    (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount)
            }

        })

        _binding.stickerGrid.setOnItemClickListener { adapterView, view, i, l ->
            val sticker = stickerData[i]

            Stipop.send(sticker.stickerId, sticker.keyword) { result ->
                if (result) {
                    Stipop.instance!!.delegate.onStickerSelected(sticker)

                    finish()
                }
            }
        }

        if (Config.searchTagsHidden) {
            _binding.tagLL.visibility = View.GONE
        } else {
            _binding.tagLL.visibility = View.VISIBLE

            getKeyword()
        }

        search("")
    }

    private fun getKeyword() {
        keywords.clear()

        val params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(
            this,
            APIClient.APIPath.SEARCH_KEYWORD.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            // println(response)

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    val keywordList = body.getJSONArray("keywordList")

                    for (i in 0 until keywordList.length()) {
                        keywords.add(keywordList.get(i) as JSONObject)
                    }

                }

            }

            keywordAdapter.notifyDataSetChanged()

        }

    }

    private fun search(keyword: String) {

        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)
        params.put("limit", 36)
        params.put("pageNumber", page)
        params.put("q", keyword)

        APIClient.get(
            this,
            APIClient.APIPath.SEARCH.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            // println(response)

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        totalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("stickerList")) {
                        val stickerList = body.getJSONArray("stickerList")

                        if (stickerList.length() < 1) {
                            return@get
                        }

                        if (page == 1) {
                            stickerData.clear()
                        }

                        for (i in 0 until stickerList.length()) {
                            stickerData.add(SPSticker(stickerList.get(i) as JSONObject))
                        }

                    }

                }

            }

            stickerAdapter.notifyDataSetChanged()

        }

    }


}
