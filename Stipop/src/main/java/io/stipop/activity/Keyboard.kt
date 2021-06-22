package io.stipop.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.APIClient
import io.stipop.R
import io.stipop.Stipop
import io.stipop.Utils
import io.stipop.adapter.*
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import org.json.JSONObject
import java.io.IOException

class Keyboard(val activity: Activity) : PopupWindow() {

    private lateinit var rootView: View

    private lateinit var packageRV: RecyclerView
    private lateinit var stickerGV: GridView

    lateinit var packageAdapter: KeyboardPackageAdapter
    lateinit var stickerAdapter: StickerAdapter

    var packageData = ArrayList<SPPackage>()
    var stickerData = ArrayList<SPSticker>()

    var selectedPackageId = -1
    var page = 1
    var totalPage = 1

    companion object {
        fun show(activity: Activity) {
            val keyboard = Keyboard(activity)
            keyboard.show()
        }
    }

    fun show() {

        if (Stipop.keyboardHeight == 0) {
            return
        }

        val view = View.inflate(this.activity, R.layout.keyboard,null)

        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }


        // animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.BOTTOM
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.BOTTOM
            popupWindow.exitTransition = slideOut
        }

        // set size
        popupWindow.height = Stipop.keyboardHeight

        packageRV = view.findViewById(R.id.packageRV)
        stickerGV = view.findViewById(R.id.stickerGV)


        val mLayoutManager = LinearLayoutManager(this.activity)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        packageRV.layoutManager = mLayoutManager

        packageAdapter = KeyboardPackageAdapter(packageData, this.activity, this)
        packageRV.adapter = packageAdapter
        packageRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = packageData.size

                if (lastVisibleItemPosition + 1 == itemTotalCount && totalPage > page) {
                    // 리스트 마지막 도착! 다음 페이지 로드!
                    page += 1
                    loadPackages()
                }
            }
        })
        packageAdapter.setOnItemClickListener(object : KeyboardPackageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (position > packageData.size) {
                    return
                }

                val item = packageData[position]

                selectedPackageId = item.packageId

                packageAdapter.notifyDataSetChanged()

                loadStickers()
            }
        })


        stickerAdapter = StickerAdapter(this.activity, R.layout.item_sticker, stickerData)
        stickerGV.adapter = stickerAdapter


        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        // Events

        view.findViewById<Button>(R.id.button_popup).setOnClickListener {
            popupWindow.dismiss()
        }

        loadPackages()



        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////



        // show
        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View
        popupWindow.showAtLocation(
            this.rootView,
            Gravity.BOTTOM,
            0,
            0
        )
    }

    private fun loadPackages() {

        val params = JSONObject()
        params.put("pageNumber", page)
        params.put("limit", 20)

        APIClient.get(
            activity,
            APIClient.APIPath.MY_STICKER.rawValue + "/${Stipop.userId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (page == 1) {
                packageData.clear()
                packageAdapter.notifyDataSetChanged()
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
                            packageData.add(SPPackage(packageList.get(i) as JSONObject))
                        }

                        packageAdapter.notifyDataSetChanged()

                        if (selectedPackageId < 1 && packageData.size > 0) {
                            selectedPackageId = packageData[0].packageId
                            loadStickers()
                        }
                    }
                }
            }

        }

    }

    private fun loadStickers() {

        stickerData.clear()
        stickerAdapter.notifyDataSetChanged()

        val params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(
            activity,
            APIClient.APIPath.PACKAGE.rawValue + "/$selectedPackageId",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")
                    val packageObj = body.getJSONObject("package")

                    if (!packageObj.isNull("stickers")) {
                        val stickers = packageObj.getJSONArray("stickers")

                        for (i in 0 until stickers.length()) {
                            stickerData.add(SPSticker(stickers.get(i) as JSONObject))
                        }

                        stickerAdapter.notifyDataSetChanged()

                    }

                }
            }

        }

    }
}