package io.stipop.refactor.present.ui.pages.search_sticker

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
import io.stipop.Config
import io.stipop.R
import io.stipop.Utils
import io.stipop.databinding.ActivitySearchStickerBinding
import io.stipop.extend.RecyclerDecoration
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.present.ui.adapters.KeywordAdapter
import io.stipop.refactor.present.ui.adapters.StickerAdapter
import org.json.JSONObject

class SPSearchStickerActivity : Activity() {

    lateinit var _binding: ActivitySearchStickerBinding

    lateinit var keywordAdapter: KeywordAdapter
    lateinit var stickerAdapter: StickerAdapter

    var keywords = ArrayList<JSONObject>()
    var stickerData = ArrayList<SPSticker>()

    private var lastItemVisibleFlag = false

    var page = 1
    var totalPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySearchStickerBinding.inflate(layoutInflater)
        setContentView(_binding.root)

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

//        stickerAdapter = StickerAdapter(_context, R.layout.item_sticker, stickerData)
//
//        val mLayoutManager = LinearLayoutManager(this)
//        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
//
//        _binding.keywordRV.layoutManager = mLayoutManager
//        _binding.keywordRV.addItemDecoration(RecyclerDecoration(10))
//        _binding.keywordRV.adapter = keywordAdapter
//
//        _binding.stickerGrid.numColumns = Config.searchNumOfColumns
//        _binding.stickerGrid.adapter = stickerAdapter
//        _binding.stickerGrid.setOnScrollListener(object : AbsListView.OnScrollListener {
//            override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && totalPage > page) {
//                    page += 1
//
//                    search(Utils.getString(_binding.keywordET))
//                }
//            }
//
//            override fun onScroll(
//                view: AbsListView?,
//                firstVisibleItem: Int,
//                visibleItemCount: Int,
//                totalItemCount: Int
//            ) {
//                lastItemVisibleFlag =
//                    (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount)
//            }
//
//        })

        _binding.stickerGrid.setOnItemClickListener { adapterView, view, i, l ->
/*
// TODO refactor

            val sticker = stickerData[i]

            Stipop.send(sticker.stickerId, sticker.keyword) { result ->
                if (result) {
                    Stipop.instance!!.delegate.onStickerSelected(sticker)

                    finish()
                }
            }
            */
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
        /*
        // TODO refactor

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
*/
    }

    private fun search(keyword: String) {
/*
// TODO refactor


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
*/

    }


}
