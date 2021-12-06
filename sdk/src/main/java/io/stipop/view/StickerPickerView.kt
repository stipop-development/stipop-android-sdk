package io.stipop.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.adapter.MyPackageHorizontalAdapter
import io.stipop.adapter.StickerGridAdapter
import io.stipop.custom.HorizontalDecoration
import io.stipop.databinding.ViewKeyboardPopupBinding
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import io.stipop.view.viewmodel.SpvModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class StickerPickerView(
    private val activity: Activity,
    private val visibleStateListener: VisibleStateListener
) : PopupWindow(), MyPackageHorizontalAdapter.OnPackageClickListener,
    StickerGridAdapter.OnStickerClickListener {

    interface VisibleStateListener {
        fun onSpvVisibleState(isVisible: Boolean)
    }

    var wantShowing: Boolean = false
    private var keyboardViewModel: SpvModel
    private val previewPopup: PreviewPopup by lazy {
        PreviewPopup(
            activity,
            this@StickerPickerView
        )
    }
    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)
    private var binding: ViewKeyboardPopupBinding =
        ViewKeyboardPopupBinding.inflate(activity.layoutInflater)
    private val packageThumbnailHorizontalAdapter: MyPackageHorizontalAdapter by lazy {
        MyPackageHorizontalAdapter(
            this
        )
    }

    fun invalidate() {
        update(this.width, this.height)
    }

    private val stickerThumbnailAdapter: StickerGridAdapter by lazy { StickerGridAdapter(this) }
    private val decoration =
        HorizontalDecoration(StipopUtils.dpToPx(8F).toInt(), StipopUtils.dpToPx(8F).toInt())

    init {
        contentView = binding.root
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = Stipop.currentKeyboardHeight
        keyboardViewModel = SpvModel()
        applyTheme()
        with(binding) {
            packageThumbRecyclerView.run {
                setHasFixedSize(true)
                setItemViewCacheSize(20)
                adapter = packageThumbnailHorizontalAdapter
            }
            stickerRecyclerView.run {
                addItemDecoration(decoration)
                setHasFixedSize(true)
                adapter = stickerThumbnailAdapter
            }
            recentFavoriteContainer.setOnClickListener {
                onRecentFavoriteClick()
            }
            storeImageView.setOnClickListener {
                showStore(0)
            }
        }
        ioScope.launch {
            keyboardViewModel.loadMyPackages().collectLatest {
                launch(Dispatchers.Main) {
                    packageThumbnailHorizontalAdapter.submitData(it)
                }
            }
        }
        packageThumbnailHorizontalAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                initialize(positionStart == 0)
                binding.packageThumbRecyclerView.scrollToPosition(0)
            }
        })
    }

    internal fun show() {
        if (isShowing) {
            return
        }
        if (Stipop.currentKeyboardHeight > 0) {
            refreshData()
            showAtLocation(
                activity.window.decorView.findViewById(android.R.id.content) as View,
                Gravity.BOTTOM,
                0,
                0
            )
            visibleStateListener.onSpvVisibleState(true)
            keyboardViewModel.trackSpv()
        }
    }

    override fun dismiss() {
        wantShowing = false
        super.dismiss()
        packageThumbnailHorizontalAdapter.updateSelected()
        visibleStateListener.onSpvVisibleState(false)
    }

    private fun refreshData() {
        loadRecentOrFavorite(false)
        packageThumbnailHorizontalAdapter.updateSelected()
        packageThumbnailHorizontalAdapter.refresh()
    }

    private fun applyRecentFavoriteTheme() {
        with(binding) {
            recentFavoriteContainer.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            when (Config.showPreview) {
                true -> {
                    if (recentFavoriteContainer.tag == 1) {
                        favoriteIV.setIconDefaultsColor()
                        recentlyIV.setIconDefaultsColor40Opacity()
                    } else {
                        recentlyIV.setIconDefaultsColor()
                        favoriteIV.setIconDefaultsColor40Opacity()
                    }
                }
                false -> {
                    recentStickerImageView.setTint()
                }
            }
        }
    }

    internal fun changeFavorite(stickerId: Int, favoriteYN: String, packageId: Int) {
        stickerThumbnailAdapter.updateFavorite(stickerId, favoriteYN)?.let {
            StipopUtils.saveStickerAsJson(activity, it, packageId)
        }
    }

    private fun showStickers(selectedPackage: StickerPackage) {
        binding.emptyListTextView.isVisible = false
        binding.progressBar.isVisible = false
        val stickerList = StipopUtils.getStickersFromLocal(activity, selectedPackage.packageId)
        stickerThumbnailAdapter.updateDatas(if (stickerList.isEmpty()) selectedPackage.stickers else stickerList)
        if (stickerList.isEmpty()) {
            ioScope.launch {
                StipopUtils.downloadAtLocal(selectedPackage) { }
            }
        }
    }

    private fun onRecentFavoriteClick() {
        packageThumbnailHorizontalAdapter.updateSelected()
        binding.progressBar.isVisible = true
        applyRecentFavoriteTheme()
        if (Config.showPreview) {
            if (binding.recentFavoriteContainer.tag == 0) {
                binding.recentFavoriteContainer.tag = 1
            } else {
                binding.recentFavoriteContainer.tag = 0
            }
        } else {
            binding.recentFavoriteContainer.tag = 0
        }
        loadRecentOrFavorite(true)
    }

    private fun loadRecentOrFavorite(isClicked: Boolean) {
        stickerThumbnailAdapter.clearData()
        if (binding.recentFavoriteContainer.tag == 1) {
            keyboardViewModel.loadFavorites(onSuccess = {
                binding.progressBar.isVisible = false
                if (it.isEmpty()) {
                    initialize(!isClicked)
                } else {
                    applyRecentFavoriteTheme()
                    it.forEach {
                        stickerThumbnailAdapter.updateData(it)
                    }
                }
            })
        } else {
            keyboardViewModel.loadRecent(onSuccess = {
                binding.progressBar.isVisible = false
                if (it.isEmpty()) {
                    binding.emptyListTextView.isVisible = true
                    initialize(!isClicked)
                } else {
                    applyRecentFavoriteTheme()
                    it.forEach {
                        stickerThumbnailAdapter.updateData(it)
                    }
                }
            })
        }
    }

    override fun onPackageClick(position: Int, stickerPackage: StickerPackage) {
        with(binding) {
            emptyListTextView.isVisible = false
            progressBar.isVisible = true
            recentFavoriteContainer.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            recentStickerImageView.clearTint()
        }
        packageThumbnailHorizontalAdapter.updateSelected(position)
        stickerThumbnailAdapter.clearData()
        keyboardViewModel.loadStickerPackage(stickerPackage, onSuccess = {
            it?.let {
                showStickers(it)
            }
        })
    }

    override fun onStickerClick(position: Int, spSticker: SPSticker) {
        Stipop.send(
            spSticker.stickerId,
            spSticker.keyword,
            Constants.Point.PICKER_VIEW
        ) { result ->
            if (result) {
                if (Config.showPreview) {
                    previewPopup.sticker = spSticker
                    if (previewPopup.windowIsShowing()) {
                        previewPopup.setStickerView()
                    } else {
                        previewPopup.show()
                    }
                } else {
                    keyboardViewModel.saveRecent(spSticker)
                    Stipop.instance?.delegate?.onStickerSelected(spSticker)
                }
            }
        }
    }

    private fun showStore(startingPosition: Int) {
        dismiss()
        Intent(activity, StoreActivity::class.java).apply {
            putExtra(Constants.IntentKey.STARTING_TAB_POSITION, startingPosition)
        }.run {
            activity.startActivity(this)
        }
    }

    private fun applyTheme() {
        with(binding) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.indeterminateTintList =
                    ColorStateList.valueOf(Color.parseColor(Config.themeMainColor))
            }
            containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            packageListHeader.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            storeImageView.setImageResource(Config.getKeyboardStoreResourceId(activity))
            storeImageView.setIconDefaultsColor()
            favoriteIV.setImageResource(R.mipmap.ic_favorites_active)
            recentlyIV.setImageResource(R.mipmap.ic_recents_active)
            recentFavoriteContainer.tag = 0
            when (Config.showPreview) {
                true -> {
                    recentStickerImageView.visibility = View.GONE
                    recentlyIV.visibility = View.VISIBLE
                    favoriteIV.visibility = View.VISIBLE
                }
                false -> {
                    recentStickerImageView.visibility = View.VISIBLE
                    recentlyIV.visibility = View.GONE
                    favoriteIV.visibility = View.GONE
                }
            }
            stickerRecyclerView.layoutManager =
                GridLayoutManager(activity, Config.keyboardNumOfColumns)
        }
        applyRecentFavoriteTheme()
    }

    private fun initialize(isFirst: Boolean? = false) {
        if (isFirst == true) {
            if (keyboardViewModel.recentStickers.isEmpty() && !packageThumbnailHorizontalAdapter.isSelectedItemExist()) {
                packageThumbnailHorizontalAdapter.getItemByPosition(0)?.let {
                    onPackageClick(0, it)
                }
            }
        }
    }
}