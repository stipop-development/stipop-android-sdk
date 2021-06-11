package io.stipop.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.stipop.*
import io.stipop.adapter.KeywordAdapter
import io.stipop.adapter.StickerAdapter
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import java.io.IOException

class SearchActivity: AppCompatActivity() {

    lateinit var context: Context

    lateinit var keywordAdapter: KeywordAdapter
    lateinit var stickerAdapter: StickerAdapter

    var keywords = ArrayList<JSONObject>()
    var stickerData = ArrayList<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        this.context = this

        keywordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val keyword = Utils.getString(keywordET)

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
                search(keyword)
            }
        })

        stickerAdapter = StickerAdapter(context, R.layout.item_sticker, stickerData)

        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        keywordRV.layoutManager = mLayoutManager
        keywordRV.addItemDecoration(RecyclerDecoration(10))
        keywordRV.adapter = keywordAdapter

        stickerGV.adapter = stickerAdapter

        getKeyword()
        search("")
    }

    fun getKeyword() {
        keywords.clear()

        APIClient.get(this, APIClient.APIPath.SEARCH_KEYWORD.rawValue, null) { response: JSONObject?, e: IOException? ->
            println(response)

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    val keywordList = body.getJSONArray("keywordList")

                    for (i in 0 until keywordList.length()) {
                        keywords.add(keywordList.get(i) as JSONObject)
                    }

                }

            } else {

            }

            keywordAdapter.notifyDataSetChanged()

        }

    }

    fun search(keyword: String) {

        stickerData.clear()

        var params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)
        params.put("q", keyword)

        APIClient.get(this, APIClient.APIPath.SEARCH.rawValue, params) { response: JSONObject?, e: IOException? ->
            println(response)

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("stickerList")) {
                        val stickerList = body.getJSONArray("stickerList")

                        for (i in 0 until stickerList.length()) {
                            stickerData.add(stickerList.get(i) as JSONObject)
                        }
                    }

                }

            } else {

            }

            stickerAdapter.notifyDataSetChanged()

        }

    }


}