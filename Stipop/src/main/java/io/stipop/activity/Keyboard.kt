package io.stipop.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.adapter.*
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import org.json.JSONObject
import java.io.IOException

class Keyboard(val activity: Activity) : PopupWindow() {

    private lateinit var rootView: View

    private lateinit var favoriteRL: RelativeLayout
    private lateinit var recentlyIV: StipopImageView
    private lateinit var favoriteIV: StipopImageView
    private lateinit var recentPreviewOffIV: StipopImageView
    private lateinit var storeIV: StipopImageView

    private lateinit var packageRV: RecyclerView
    private lateinit var stickerGV: GridView
    private lateinit var downloadLL: LinearLayout
    private lateinit var packageIV: StipopImageView
    private lateinit var packageNameTV: TextView
    private lateinit var artistNameTV: TextView
    private lateinit var downloadTV: TextView

    lateinit var packageAdapter: KeyboardPackageAdapter
    lateinit var stickerAdapter: StickerAdapter

    var packageData = ArrayList<SPPackage>()
    var stickerData = ArrayList<SPSticker>()

    var selectedPackage:SPPackage? = null
    var selectedPackageId = -1
    var page = 1
    var totalPage = 1

    private var lastItemVisibleFlag = false
    var stickerPage = 1
    var stickerTotalPage = 1

    lateinit var preview: Preview

    lateinit var popupWindow:PopupWindow
    internal var canShow = true

    init {
        this.initPopup()
    }

    var reloadPackageReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                reloadPackages()
            }
        }
    }

    private fun initPopup() {

        val view = View.inflate(this.activity, R.layout.keyboard,null)

        popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        popupWindow.elevation = 10.0F


        // animations
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.BOTTOM
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.BOTTOM
            popupWindow.exitTransition = slideOut
        }
        */

        // set size
        popupWindow.height = Stipop.keyboardHeight

        preview = Preview(this.activity, this)

        view.findViewById<LinearLayout>(R.id.containerLL).setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
        view.findViewById<LinearLayout>(R.id.packageListLL).setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))

        favoriteRL = view.findViewById(R.id.favoriteRL)
        recentlyIV = view.findViewById(R.id.recentlyIV)
        favoriteIV = view.findViewById(R.id.favoriteIV)
        recentPreviewOffIV = view.findViewById(R.id.recentPreviewOffIV)
        storeIV = view.findViewById(R.id.storeIV)

        packageRV = view.findViewById(R.id.packageRV)
        stickerGV = view.findViewById(R.id.stickerGV)
        downloadLL = view.findViewById(R.id.downloadLL)
        packageIV = view.findViewById(R.id.packageIV)
        packageNameTV = view.findViewById(R.id.packageNameTV)
        artistNameTV = view.findViewById(R.id.artistNameTV)
        downloadTV = view.findViewById(R.id.downloadTV)

        val drawable2 = downloadTV.background as GradientDrawable
        drawable2.setColor(Color.parseColor(Config.themeMainColor)) // solid  color

        storeIV.setImageResource(Config.getKeyboardStoreResourceId(this.activity))
        storeIV.setIconDefaultsColor()


        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        // Events

        if (!Config.showPreview) {
            recentPreviewOffIV.visibility = View.VISIBLE

            recentlyIV.visibility = View.GONE
            favoriteIV.visibility = View.GONE
        } else {
            recentPreviewOffIV.visibility = View.GONE

            recentlyIV.visibility = View.VISIBLE
            favoriteIV.visibility = View.VISIBLE
        }

        favoriteRL.tag = 0
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

                // println(packageData)

                val pack = packageData[position]

                if (pack.packageId == -999) {
                    showStore(2)
                } else {
                    selectedPackageId = pack.packageId

                    packageAdapter.notifyDataSetChanged()

                    loadStickers()
                }
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
        stickerGV.setOnItemClickListener { adapterView, view, i, l ->
            val sticker = stickerData[i]

            Stipop.send(sticker.stickerId, sticker.keyword) { result ->
                if (result) {
                    if (Config.showPreview) {
                        preview.sticker = sticker

                        if (preview.windowIsShowing()) {
                            preview.setStickerView()
                        } else {
                            preview.show()
                        }
                    } else {
                        // delegate
                        Stipop.instance!!.delegate.onStickerSelected(sticker)
                    }

                }

            }

        }

        view.findViewById<Button>(R.id.button_popup).setOnClickListener {
            popupWindow.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.storeLL).setOnClickListener {
            showStore(1)
        }

        favoriteRL.setOnClickListener {
            favoriteRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))

            if (selectedPackageId == -1 && Config.showPreview) {
                if (favoriteRL.tag == 0) {
                    favoriteRL.tag = 1
                } else {
                    favoriteRL.tag = 0
                }
            }

            selectedPackageId = -1
            packageAdapter.notifyDataSetChanged()

            stickerPage = 1

            stickerData.clear()
            stickerAdapter.notifyDataSetChanged()

            loadFavoriteRecently()
        }

        downloadTV.setOnClickListener {
            // download
            PackUtils.downloadAndSaveLocal(this.activity, this.selectedPackage) {
                showStickers()
            }
        }

        reloadPackages()

        val broadcastIntentFilter = IntentFilter("${this.activity.packageName}.RELOAD_PACKAGE_LIST_NOTIFICATION")
        this.activity.registerReceiver(reloadPackageReciver, broadcastIntentFilter)

        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////



        // show
        // this.showOrHide()
    }

    internal fun show() {
        if (Stipop.keyboardHeight == 0) {
            return
        }

        if (!this.canShow) {
            return
        }

        reloadPackages()

        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View
        popupWindow.showAtLocation(
            this.rootView,
            Gravity.BOTTOM,
            0,
            0
        )
    }

    internal fun hide() {
        if (Stipop.keyboardHeight == 0) {
            return
        }

        popupWindow.dismiss()
    }

    fun showStore(tab: Int) {

        this.hide()

        val intent = Intent(this.activity, StoreActivity::class.java)
        intent.putExtra("tab", tab)
        this.activity.startActivity(intent)
    }

    fun changeFavorite(stickerId: Int, favoriteYN: String, packageId: Int) {
        for (i in 0 until stickerData.size) {
            if (stickerData[i].stickerId == stickerId) {
                stickerData[i].favoriteYN = favoriteYN

                PackUtils.saveStickerJsonData(this.activity, stickerData[i], packageId)
                break
            }
        }
    }

    private fun setThemeImageIcon() {
//        if (Config.useLightMode) {
//            recentlyIV.setImageResource(R.mipmap.ic_recents_normal)
//            favoriteIV.setImageResource(R.mipmap.ic_favorites_normal)
//            if (favoriteRL.tag == 1) {
//                favoriteIV.setImageResource(R.mipmap.ic_favorites_active)
//            } else {
//                recentlyIV.setImageResource(R.mipmap.ic_recents_active)
//            }
//        } else {
//            recentlyIV.setImageResource(R.mipmap.ic_recents_normal_dark)
//            favoriteIV.setImageResource(R.mipmap.ic_favorites_normal_dark)
//
//            if (favoriteRL.tag == 1) {
//                favoriteIV.setImageResource(R.mipmap.ic_favorites_active_dark)
//            } else {
//                recentlyIV.setImageResource(R.mipmap.ic_recents_active_dark)
//            }
//        }

        favoriteIV.setImageResource(R.mipmap.ic_favorites_active)
        recentlyIV.setImageResource(R.mipmap.ic_recents_active)

        if (favoriteRL.tag == 1) {
            favoriteIV.setIconDefaultsColor()
            recentlyIV.setIconDefaultsColor40Opacity()
        } else {
            recentlyIV.setIconDefaultsColor()
            favoriteIV.setIconDefaultsColor40Opacity()
        }
    }

    private fun reloadPackages() {

        packageData.clear()
        packageAdapter.notifyDataSetChanged()

        page = 1

        loadPackages()
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
                        // println(packageList.toString())

                        for (i in 0 until packageList.length()) {
                            packageData.add(SPPackage(packageList.get(i) as JSONObject))
                        }
                    }
                }

                if (page == totalPage) {
                    packageData.add(SPPackage(-999))
                }

                var isSelectedTabValid = false
                for (spPackage in packageData) {
                    if (spPackage.packageId == selectedPackageId) {
                        isSelectedTabValid = true
                        break
                    }
                }

                // println("isSelectedTabValid : $isSelectedTabValid")

                if (isSelectedTabValid) {
                    if (selectedPackageId == -1) {
                        stickerPage = 1
                        loadRecently()
                    }
                } else {
                    selectedPackageId = -1
                    stickerPage = 1
                    loadRecently()
                }

                packageAdapter.notifyDataSetChanged()

            } else {
                e?.printStackTrace()
            }

        }

    }

    private fun showStickers() {

        stickerData.clear()
        stickerAdapter.notifyDataSetChanged()

        val stickerList = PackUtils.stickerListOf(this.activity, selectedPackageId)
        if (stickerList.size == 0) {
            stickerGV.visibility = View.GONE
            downloadLL.visibility = View.VISIBLE

            val packageImg = selectedPackage!!.packageImg
            val packageName = selectedPackage!!.packageName
            val artistName = selectedPackage!!.artistName

            Glide.with(this.activity).load(packageImg).into(packageIV)
            packageNameTV.text = packageName
            artistNameTV.text = artistName

        } else {
            stickerGV.visibility = View.VISIBLE
            downloadLL.visibility = View.GONE

            for (i in 0 until stickerList.size) {
                stickerData.add(stickerList[i])
            }

            stickerAdapter.notifyDataSetChanged()
        }

    }

    private fun loadStickers() {
        favoriteRL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))

        stickerData.clear()

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
                    this.selectedPackage = SPPackage(body.getJSONObject("package"))

                    showStickers()
                }
            }

        }

    }

    private fun loadFavoriteRecently() {
        setThemeImageIcon()

        selectedPackageId = -1

        // Favorite
        if (favoriteRL.tag == 1) {
            loadFavorite()
        } else {
            // Recently
            loadRecently()
        }
    }

    private fun loadFavorite() {

        favoriteRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))

        downloadLL.visibility = View.GONE
        stickerGV.visibility = View.VISIBLE

        val params = JSONObject()
        params.put("pageNumber", stickerPage)
        params.put("limit", 12)

        APIClient.get(
            activity,
            APIClient.APIPath.MY_STICKER_FAVORITE.rawValue + "/${Stipop.userId}",
            null
        ) { response: JSONObject?, e: IOException? ->

            // println(response)

            if (stickerPage == 1) {
                stickerData.clear()
                stickerAdapter.notifyDataSetChanged()
            }

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")

                    if (!response.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        stickerTotalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("favoriteList")) {
                        val favoriteList = body.getJSONArray("favoriteList")

                        for (i in 0 until favoriteList.length()) {
                            stickerData.add(SPSticker(favoriteList.get(i) as JSONObject))
                        }

                        stickerAdapter.notifyDataSetChanged()

                    }

                }
            }

        }
    }

    private fun loadRecently() {
        favoriteRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))

        downloadLL.visibility = View.GONE
        stickerGV.visibility = View.VISIBLE

        val params = JSONObject()
        params.put("pageNumber", stickerPage)
        params.put("limit", 12)

        APIClient.get(
            activity,
            APIClient.APIPath.PACKAGE_SEND.rawValue + "/${Stipop.userId}",
            null
        ) { response: JSONObject?, e: IOException? ->

            if (stickerPage == 1) {
                stickerData.clear()
                stickerAdapter.notifyDataSetChanged()
            }

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


    override fun dismiss() {
        if (reloadPackageReciver != null) {
            this.activity.unregisterReceiver(reloadPackageReciver)
        }
        super.dismiss()
    }
}