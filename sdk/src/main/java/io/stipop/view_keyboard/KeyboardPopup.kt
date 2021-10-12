package io.stipop.view_keyboard

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.adapter.legacy.StickerPackageThumbnailAdapter
import io.stipop.adapter.legacy.StickerAdapter
import io.stipop.api.APIClient
import io.stipop.api.StipopApi
import io.stipop.databinding.ViewKeyboardPopupBinding
import io.stipop.models.SPPackage
import io.stipop.models.SPSticker
import io.stipop.models.body.UserIdBody
import io.stipop.view_store.StoreActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class KeyboardPopup(val activity: Activity) : PopupWindow(),
    StickerPackageThumbnailAdapter.OnPackageClickListener {


    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private var binding: ViewKeyboardPopupBinding = ViewKeyboardPopupBinding.inflate(activity.layoutInflater)
    val packageThumbnailAdapter: StickerPackageThumbnailAdapter by lazy { StickerPackageThumbnailAdapter(this) }
    var stickerAdapter: StickerAdapter
    var previewPopup: PreviewPopup
    var stickerData = ArrayList<SPSticker>()
    var selectedPackage: SPPackage? = null
    var selectedPackageId = -1
    var page = 1
    var totalPage = 1
    private var lastItemVisibleFlag = false
    var stickerPage = 1
    var stickerTotalPage = 1
    internal var canShow = true

    private var reloadPackageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                reloadPackages()
            }
        }
    }

    init {
        contentView = binding.root
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = Stipop.keyboardHeight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) elevation = 10.0F

        with(binding) {
            containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            packageListLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            val animator: RecyclerView.ItemAnimator? = packageThumbRecyclerView.itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            packageThumbRecyclerView.setHasFixedSize(true)
            packageThumbRecyclerView.setItemViewCacheSize(20)
            val drawable2 = downloadTV.background as GradientDrawable
            drawable2.setColor(Color.parseColor(Config.themeMainColor)) // solid  color
            storeIV.setImageResource(Config.getKeyboardStoreResourceId(activity))
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

            packageThumbRecyclerView.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            packageThumbnailAdapter.setHasStableIds(true)
            packageThumbnailAdapter.setOnItemClickListener(this@KeyboardPopup)
            packageThumbRecyclerView.adapter = packageThumbnailAdapter

            packageThumbRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = packageThumbnailAdapter.itemCount
                    if (lastVisibleItemPosition + 1 == itemTotalCount && totalPage > page) {
                        // 리스트 마지막 도착! 다음 페이지 로드!
                        page += 1
                        loadPackages()
                    }
                }
            })
            previewPopup = PreviewPopup(activity, this@KeyboardPopup)
            stickerAdapter = StickerAdapter(activity, R.layout.item_sticker, stickerData)
            stickerGV.numColumns = Config.keyboardNumOfColumns
            stickerGV.adapter = stickerAdapter
            stickerGV.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && stickerTotalPage > stickerPage) {
                        stickerPage += 1
                        loadFavoriteRecently()
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
            stickerGV.setOnItemClickListener { adapterView, view, i, l ->
                val sticker = stickerData[i]
                Stipop.send(sticker.stickerId, sticker.keyword) { result ->
                    if (result) {
                        if (Config.showPreview) {
                            previewPopup.sticker = sticker

                            if (previewPopup.windowIsShowing()) {
                                previewPopup.setStickerView()
                            } else {
                                previewPopup.show()
                            }
                        } else {
                            // delegate
                            Stipop.instance!!.delegate.onStickerSelected(sticker)
                        }

                    }
                }
            }

            buttonPopup.setOnClickListener {
                dismiss()
            }

            storeLL.setOnClickListener {
                showStore(0)
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
                packageThumbnailAdapter.notifyDataSetChanged()

                stickerPage = 1

                stickerData.clear()
                stickerAdapter.notifyDataSetChanged()

                loadFavoriteRecently()
            }
            downloadTV.setOnClickListener {
                // download
                PackUtils.downloadAndSaveLocal(activity, selectedPackage) {
                    showStickers()
                }
            }
        }



        reloadPackages()

        activity.registerReceiver(
            reloadPackageReceiver,
            IntentFilter("${this.activity.packageName}.RELOAD_PACKAGE_LIST_NOTIFICATION")
        )
            .also { Log.d("DEBUGGING", "REGISTER RECEIVER") }
    }


    internal fun show() {
        if (!this.canShow) {
            return
        }

        reloadPackages()

        val rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View

        if (Stipop.keyboardHeight > 0) {
            showAtLocation(
                rootView,
                Gravity.BOTTOM,
                0,
                0
            )
        }
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        scope.launch {
            StipopApi.create().trackViewPicker(UserIdBody(Stipop.userId))
        }
    }

    internal fun hide() {
        dismiss()
    }

    fun showStore(tab: Int) {
        this.hide()
        val intent = Intent(this.activity, StoreActivity::class.java)
        intent.putExtra(Constants.IntentKey.STARTING_TAB_POSITION, tab)
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
        with(binding) {
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
    }

    private fun clearThemeImageIcon() {
        binding.recentPreviewOffIV.clearTint()
    }

    private fun reloadPackages() {
        packageThumbnailAdapter.clearData()
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
                packageThumbnailAdapter.clearData()
            }
            if (null != response) {
                val data: ArrayList<SPPackage> = ArrayList()
                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")

                    if (!response.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        totalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("packageList")) {
                        val packageList = body.getJSONArray("packageList")
                        for (i in 0 until packageList.length()) {
                            data.add(SPPackage(packageList.get(i) as JSONObject))
                        }
                    }
                }
                if (page == totalPage) {
                    data.add(SPPackage(-999))
                }
                var isSelectedTabValid = false
                for (spPackage in data) {
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
                packageThumbnailAdapter.setData(data)
            } else {
                e?.printStackTrace()
            }

        }

    }

    private fun showStickers() {
        with(binding) {
            stickerData.clear()
            stickerAdapter.notifyDataSetChanged()

            val stickerList = PackUtils.stickerListOf(activity, selectedPackageId)
            if (stickerList.size == 0) {
                stickerGV.visibility = View.GONE
                downloadLL.visibility = View.VISIBLE

                val packageImg = selectedPackage!!.packageImg
                val packageName = selectedPackage!!.packageName
                val artistName = selectedPackage!!.artistName

                Glide.with(activity).load(packageImg).into(packageIV)
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
    }

    private fun loadStickers() {
        binding.favoriteRL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))

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
        if (binding.favoriteRL.tag == 1) {
            loadFavorite()
        } else {
            // Recently
            loadRecently()
        }
    }

    private fun loadFavorite() {
        with(binding) {
            favoriteRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            downloadLL.visibility = View.GONE
            stickerGV.visibility = View.VISIBLE
        }

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
        with(binding) {
            favoriteRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            downloadLL.visibility = View.GONE
            stickerGV.visibility = View.VISIBLE
        }

        val params = JSONObject()
        params.put("pageNumber", stickerPage)
        params.put("limit", 12)

        APIClient.get(
            activity,
            APIClient.APIPath.PACKAGE_RECENT.rawValue + "/${Stipop.userId}",
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

    override fun onItemClick(position: Int, data: SPPackage) {
        if (position > packageThumbnailAdapter.itemCount) {
            return
        }
        clearThemeImageIcon()
        if (data.packageId == -999) {
            showStore(1)
        } else {
            selectedPackageId = data.packageId
            packageThumbnailAdapter.notifyDataSetChanged()
            loadStickers()
        }
    }
}