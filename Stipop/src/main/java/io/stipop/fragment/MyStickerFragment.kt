package io.stipop.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.activity.DetailActivity
import io.stipop.adapter.MyStickerAdapter
import io.stipop.adapter.PackageAdapter
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
    var page = 1
    var totalPage = 1

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

                if (lastVisibleItemPosition + 1 == itemTotalCount && totalPage > page) {
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
        listRV.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                println("TEST===============")
                return  false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                println("TEST@@@@@@@@@@@@@@@@")
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                println("TEST!!!!!!!!!!!!!!!!!!")
            }

        })

        loadMySticker()
    }

    fun loadMySticker() {
        val params = JSONObject()
        params.put("pageNumber", page)
        params.put("limit", 20)

        APIClient.get(
            activity as Activity,
            APIClient.APIPath.MY_STICKER.rawValue + "/${Stipop.userId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (page == 1) {
                data.clear()
                myStickerAdapter.notifyDataSetChanged()
            }

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")

                    if (!response.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        totalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("packageList")) {
                        val packageList = body.getJSONArray("packageList")
                        println(packageList.toString())

                        for (i in 0 until packageList.length()) {
                            data.add(SPPackage(packageList.get(i) as JSONObject))
                        }

                        println(data)

                        myStickerAdapter.notifyDataSetChanged()
                    }
                }
            }

        }
    }

    fun myStickerOrder(fromPosition: Int, toPosition: Int) {

        val fromPackageObj = data[fromPosition]
        val toPackageObj = data[toPosition]

        val fromOrder = fromPackageObj.order
        val toOrder = toPackageObj.order

        val params = JSONObject()
        params.put("currentOrder", fromOrder)
        params.put("newOrder", toOrder)

        APIClient.put(
            activity as Activity,
            APIClient.APIPath.MY_STICKER_ORDER.rawValue + "/${Stipop.userId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {

                println(response)

                val header = response.getJSONObject("header")
                val status = Utils.getString(header, "status")

                if (status == "fail") {
                    Toast.makeText(myContext, "ERROR!!", Toast.LENGTH_LONG).show()
                } else {
                    // order 변경

                    if (!response.isNull("body")) {
                        val body = response.getJSONObject("body")
                        if (!body.isNull("packageList")) {
                            val packageList = body.getJSONArray("packageList")

                            for (i in 0 until packageList.length()) {
                                val resPackage = SPPackage(packageList[i] as JSONObject)
                                for (j in 0 until data.size) {
                                    if (resPackage.packageId == data[j].packageId) {
                                        data[j].order = resPackage.order
                                        break
                                    }
                                }
                            }

                            data.sortBy { data -> data.order }

                            myStickerAdapter.notifyDataSetChanged()
                        }
                    }

                }

            }

        }

    }

    fun hidePackage(packageId: Int) {
        val params = JSONObject()

        APIClient.put(
            activity as Activity,
            APIClient.APIPath.MY_STICKER_HIDE.rawValue + "/${Stipop.userId}/$packageId",
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