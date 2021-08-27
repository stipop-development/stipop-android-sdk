package io.stipop.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.PackUtils
import io.stipop.R
import io.stipop.Stipop
import io.stipop.extend.StipopImageView
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.present.ui.adapters.KeyboardPackageAdapter
import io.stipop.refactor.present.ui.adapters.StickerAdapter
import io.stipop.refactor.present.ui.pages.store.StoreActivity
import io.stipop.refactor.present.ui.view_model.KeyboardViewModel
import javax.inject.Inject


class KeyboardFragment : Fragment() {

    @Inject
    internal lateinit var _viewModel: KeyboardViewModel

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

    var selectedPackage: SPPackage? = null
    var selectedPackageId = -1
    var page = 1
    var totalPage = 1

    private var lastItemVisibleFlag = false
    var stickerPage = 1
    var stickerTotalPage = 1

    lateinit var preview: Preview

    lateinit var popupWindow:PopupWindow
    internal var canShow = true

    val _activity: Activity get() = activity ?: throw Error("Hasn't activity")

    init {
        this.initPopup(null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Stipop.appComponent.inject(this)
        return inflater.inflate(R.layout.keyboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPopup(view)
    }

    var reloadPackageReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                reloadPackages()
            }
        }
    }

    private fun initPopup(v: View?) {

        var view = View.inflate(this.activity, R.layout.keyboard,null)
        if (v != null) {
            view = v
        }

        popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }

        // set size
        popupWindow.height = Stipop.keyboardHeight

        preview = Preview(_activity, this)

        view.findViewById<LinearLayout>(R.id.containerLL).setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
        view.findViewById<LinearLayout>(R.id.packageListLL).setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))

        favoriteRL = view.findViewById(R.id.favoriteRL)
        recentlyIV = view.findViewById(R.id.recentlyIV)
        favoriteIV = view.findViewById(R.id.favoriteIV)
        recentPreviewOffIV = view.findViewById(R.id.recentPreviewOffIV)
        storeIV = view.findViewById(R.id.storeIV)

        packageRV = view.findViewById(R.id.storeTrendingList)
        stickerGV = view.findViewById(R.id.sticker_grid)
        downloadLL = view.findViewById(R.id.downloadLL)
        packageIV = view.findViewById(R.id.package_image)
        packageNameTV = view.findViewById(R.id.package_name)
        artistNameTV = view.findViewById(R.id.artist_name)
        downloadTV = view.findViewById(R.id.download_button)

        val animator: RecyclerView.ItemAnimator? = packageRV.itemAnimator

        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        packageRV.setHasFixedSize(true)
        packageRV.setItemViewCacheSize(20)

        val drawable2 = downloadTV.background as GradientDrawable
        drawable2.setColor(Color.parseColor(Config.themeMainColor)) // solid  color

        storeIV.setImageResource(Config.getKeyboardStoreResourceId(_activity))
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

        val mLayoutManager = LinearLayoutManager(_activity)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        packageRV.layoutManager = mLayoutManager

        packageAdapter = KeyboardPackageAdapter(packageData, _activity, this)
        packageAdapter.setHasStableIds(true)

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

                clearThemeImageIcon()

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


        stickerAdapter = StickerAdapter(_activity, R.layout.item_sticker, stickerData)
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


            /*
            // TODO refactor
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
            */
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
            PackUtils.downloadAndSaveLocal(_activity, this.selectedPackage) {
                showStickers()
            }
        }

        reloadPackages()

        val broadcastIntentFilter = IntentFilter("${_activity.packageName}.RELOAD_PACKAGE_LIST_NOTIFICATION")
        _activity.registerReceiver(reloadPackageReciver, broadcastIntentFilter)

        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////


        // show
        // this.showOrHide()
    }

    internal fun show() {
        if (!this.canShow) {
            return
        }

        reloadPackages()

        val rootView = _activity.window.decorView.findViewById(android.R.id.content) as View

        if (Stipop.keyboardHeight > 0) {
            popupWindow.showAtLocation(
                rootView,
                Gravity.BOTTOM,
                0,
                0
            )
        }
    }

    internal fun hide() {
        popupWindow.dismiss()
    }

    fun showStore(tab: Int) {

        this.hide()

        val intent = Intent(_activity, StoreActivity::class.java)
        intent.putExtra("tab", tab)
        _activity.startActivity(intent)
    }

    fun changeFavorite(stickerId: Int, favoriteYN: String, packageId: Int) {
        for (i in 0 until stickerData.size) {
            if (stickerData[i].stickerId == stickerId) {
                stickerData[i].favoriteYN = favoriteYN

                PackUtils.saveStickerJsonData(_activity, stickerData[i], packageId)
                break
            }
        }
    }

    private fun setThemeImageIcon() {
        favoriteIV.setImageResource(R.mipmap.ic_favorites_active)
        recentlyIV.setImageResource(R.mipmap.ic_recents_active)

        if (favoriteRL.tag == 1) {
            favoriteIV.setIconDefaultsColor()
            recentlyIV.setIconDefaultsColor40Opacity()
        } else {
            recentlyIV.setIconDefaultsColor()
            favoriteIV.setIconDefaultsColor40Opacity()
        }
        recentPreviewOffIV.setTint()
    }

    private fun clearThemeImageIcon() {
        recentPreviewOffIV.clearTint()
    }

    private fun reloadPackages() {

        packageData.clear()
        packageAdapter.notifyDataSetChanged()

        page = 1

        loadPackages()
    }

    private fun loadPackages() {
/*
// TODO refactor

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
*/

    }

    private fun showStickers() {

        stickerData.clear()
        stickerAdapter.notifyDataSetChanged()

        val stickerList = PackUtils.stickerListOf(_activity, selectedPackageId)
        if (stickerList.size == 0) {
            stickerGV.visibility = View.GONE
            downloadLL.visibility = View.VISIBLE

            val packageImg = selectedPackage!!.packageImg
            val packageName = selectedPackage!!.packageName
            val artistName = selectedPackage!!.artistName

            Glide.with(_activity).load(packageImg).into(packageIV)
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
        /*
        // TODO refactor

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
*/
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
/*
// TODO refactor

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

        }*/
    }

    private fun loadRecently() {
        /*

        // TODO refactor

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
        */
    }

    override fun onDestroy() {
        if (reloadPackageReciver != null) {
            _activity.unregisterReceiver(reloadPackageReciver)
        }

        super.onDestroy()
    }

}
