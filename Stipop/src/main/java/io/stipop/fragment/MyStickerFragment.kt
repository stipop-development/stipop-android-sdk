package io.stipop.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.adapter.MyStickerAdapter
import io.stipop.extend.dragdrop.OnRecyclerAdapterEventListener
import io.stipop.extend.dragdrop.SimpleItemTouchHelperCallback
import io.stipop.model.SPPackage
import kotlinx.android.synthetic.main.fragment_my_sticker.*
import org.json.JSONObject
import java.io.IOException

class MyStickerFragment: Fragment(), OnRecyclerAdapterEventListener {

    lateinit var myContext: Context

    lateinit var myStickerAdapter: MyStickerAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    var data = ArrayList<SPPackage>()
    var page = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.myContext = container!!.context

        return inflater.inflate(R.layout.fragment_my_sticker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myStickerAdapter = MyStickerAdapter(myContext, data, this)

        listRV.layoutManager = LinearLayoutManager(myContext)
        listRV.adapter = myStickerAdapter
        listRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = data.size

                if (lastVisibleItemPosition + 1 == itemTotalCount) {
                    // 리스트 마지막 도착! 다음 페이지 로드!
                    page += 1
                    loadMySticker()
                }
            }
        })

        myStickerAdapter.setOnRecyclerAdapterEventListener(this)
        val callback = SimpleItemTouchHelperCallback(myStickerAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(listRV)

        loadMySticker()
    }

    fun loadMySticker() {
        val params = JSONObject()
        params.put("pageNumber", page)
        params.put("limit", 20)



//        params.put("userId", Stipop.userId)
//        params.put("lang", Stipop.lang)
//        params.put("countryCode", Stipop.countryCode)

        APIClient.get(
            activity as Activity,
            APIClient.APIPath.MY_STICKER.rawValue + "/" + Stipop.userId,
//            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->

            if (page == 1) {
                data.clear()
                myStickerAdapter.notifyDataSetChanged()
            }

            if (null != response) {

                println(response)

                val header = response.getJSONObject("header")
                val status = Utils.getString(header, "status")

                if (status != "fail") {
                    if (!response.isNull("body")) {
                        val body = response.getJSONObject("body")

                        if (!body.isNull("packageList")) {
                            val packageList = body.getJSONArray("packageList")

                            for (i in 0 until packageList.length()) {
                                val item = packageList.get(i) as JSONObject
                                val spPackage = SPPackage(item)
                                data.add(spPackage)
                            }

                            myStickerAdapter.notifyDataSetChanged()
                        }

                    }
                }

            }

        }
    }

    fun myStickerOrder(from: Int, to: Int) {
        val params = JSONObject()
        params.put("currentOrder", from)
        params.put("newOrder", to)

        APIClient.get(
            activity as Activity,
            APIClient.APIPath.MY_STICKER_ORDER.rawValue + "/" + Stipop.userId,
            params
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {

                println(response)

                val header = response.getJSONObject("header")
                val status = Utils.getString(header, "status")

                if (status == "fail") {
                    Toast.makeText(myContext, "ERROR!!", Toast.LENGTH_LONG).show()
                }

            }

        }

    }

    override fun onItemClicked(position: Int) {
    }

    override fun onItemLongClicked(position: Int) {
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

}