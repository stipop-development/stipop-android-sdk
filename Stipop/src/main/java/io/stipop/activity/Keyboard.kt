package io.stipop.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.adapter.*
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import kotlinx.android.synthetic.main.fragment_all_sticker.*
import org.json.JSONObject
import java.io.IOException

class Keyboard(val activity: Activity) : PopupWindow() {

    private lateinit var rootView: View

    private lateinit var favoriteRL: RelativeLayout
    private lateinit var recentlyIV: ImageView
    private lateinit var favoriteIV: ImageView
    private lateinit var storeIV: ImageView

    private lateinit var packageRV: RecyclerView
    private lateinit var stickerGV: GridView

    lateinit var packageAdapter: KeyboardPackageAdapter
    lateinit var stickerAdapter: StickerAdapter

    var packageData = ArrayList<SPPackage>()
    var stickerData = ArrayList<SPSticker>()

    var selectedPackageId = -1
    var page = 1
    var totalPage = 1

    private var lastItemVisibleFlag = false
    var stickerPage = 1
    var stickerTotalPage = 1

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

        view.findViewById<LinearLayout>(R.id.containerLL).setBackgroundColor(Color.parseColor(Config.themeContentsBgColor))
        view.findViewById<LinearLayout>(R.id.packageListLL).setBackgroundColor(Color.parseColor(Config.themeGroupedBgColor))

        favoriteRL = view.findViewById(R.id.favoriteRL)
        recentlyIV = view.findViewById(R.id.recentlyIV)
        favoriteIV = view.findViewById(R.id.favoriteIV)
        storeIV = view.findViewById(R.id.storeIV)

        packageRV = view.findViewById(R.id.packageRV)
        stickerGV = view.findViewById(R.id.stickerGV)


        storeIV.setImageResource(Config.getKeyboardStoreResourceId(this.activity))


        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        // Events

        favoriteRL.tag = false
        setThemeImageIcon()

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
        stickerGV.numColumns = Config.keyboardNumOfColumns
        stickerGV.adapter = stickerAdapter
        stickerGV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && stickerTotalPage > stickerPage) {
                    stickerPage += 1
                    loadFavoriteRecently()
                }
            }

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount)
            }

        })

        view.findViewById<Button>(R.id.button_popup).setOnClickListener {
            popupWindow.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.storeLL).setOnClickListener {
            val intent = Intent(this.activity, StoreActivity::class.java)
            this.activity.startActivity(intent)
        }

        favoriteRL.setOnClickListener {
            favoriteRL.setBackgroundColor(Color.parseColor(Config.themeContentsBgColor))

            if (selectedPackageId < 0) {
                favoriteRL.tag = !(favoriteRL.tag as Boolean)
            }

            selectedPackageId = -1
            packageAdapter.notifyDataSetChanged()

            stickerPage = 1

            stickerData.clear()
            stickerAdapter.notifyDataSetChanged()

            loadFavoriteRecently()
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

    private fun setThemeImageIcon() {
        if (Config.useLightMode) {
            recentlyIV.setImageResource(R.mipmap.ic_recents_normal)
            favoriteIV.setImageResource(R.mipmap.ic_favorites_normal)
            if (favoriteRL.tag as Boolean) {
                favoriteIV.setImageResource(R.mipmap.ic_favorites_active)
            } else {
                recentlyIV.setImageResource(R.mipmap.ic_recents_active)
            }
        } else {
            recentlyIV.setImageResource(R.mipmap.ic_recents_normal_dark)
            favoriteIV.setImageResource(R.mipmap.ic_favorites_normal_dark)

            if (favoriteRL.tag as Boolean) {
                favoriteIV.setImageResource(R.mipmap.ic_favorites_active_dark)
            } else {
                recentlyIV.setImageResource(R.mipmap.ic_recents_active_dark)
            }
        }
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
        favoriteRL.setBackgroundColor(Color.parseColor(Config.themeGroupedBgColor))

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

    private fun loadFavoriteRecently() {
        setThemeImageIcon()

        // Favorite
        if (favoriteRL.tag as Boolean) {
            loadFavorite()
        } else {
            // Recently
            loadRecently()
        }
    }

    private fun loadFavorite() {
        val params = JSONObject()
        params.put("pageNumber", stickerPage)
        params.put("limit", 12)

        APIClient.get(
            activity,
            APIClient.APIPath.FAVORITE.rawValue + "/${Stipop.userId}",
            null
        ) { response: JSONObject?, e: IOException? ->

            println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")
                    val packageObj = body.getJSONObject("package")

                    if (!response.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        stickerTotalPage = Utils.getInt(pageMap, "pageCount")
                    }

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

    private fun loadRecently() {
        val params = JSONObject()
        params.put("pageNumber", stickerPage)
        params.put("limit", 12)

        APIClient.get(
            activity,
            APIClient.APIPath.PACKAGE_SEND.rawValue + "/${Stipop.userId}",
            null
        ) { response: JSONObject?, e: IOException? ->

            println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")

                    if (!response.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        stickerTotalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("stickerList")) {
                        val stickerList = body.getJSONArray("stickerList")

                        for (i in 0 until stickerList.length()) {
                            stickerData.add(SPSticker(stickerList[i] as JSONObject))
                        }

                        stickerAdapter.notifyDataSetChanged()

                    }

                }
            }

        }
    }
}