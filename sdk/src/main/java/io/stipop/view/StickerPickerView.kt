package io.stipop.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.adapter.PagingMyPackAdapter
import io.stipop.adapter.StickerDefaultAdapter
import io.stipop.custom.DragAndDropHelperCallback
import io.stipop.custom.HorizontalDecoration
import io.stipop.databinding.ViewKeyboardPopupBinding
import io.stipop.event.MyPackEventDelegate
import io.stipop.event.PreviewDelegate
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import io.stipop.view.viewmodel.SpvModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

internal class StickerPickerView(
    private val activity: Activity,
    private val visibleDelegate: VisibleStateListener
) : PopupWindow(), MyPackEventDelegate,
    StickerDefaultAdapter.OnStickerClickListener, PreviewDelegate {

    interface VisibleStateListener {
        fun onSpvVisibleState(isVisible: Boolean)
    }

    private var binding: ViewKeyboardPopupBinding =
        ViewKeyboardPopupBinding.inflate(activity.layoutInflater)
    var wantShowing: Boolean = false
    private val keyboardViewModel: SpvModel by lazy { SpvModel() }
    private val spvPreview: SpvPreview by lazy { SpvPreview(activity, this, keyboardViewModel) }
    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)
    private val packAdapter: PagingMyPackAdapter by lazy {
        PagingMyPackAdapter(
            PagingMyPackAdapter.ViewType.SPV,
            this
        )
    }
    private val itemTouchHelper: ItemTouchHelper
    private val stickerAdapter: StickerDefaultAdapter by lazy { StickerDefaultAdapter(this) }
    private val decoration =
        HorizontalDecoration(StipopUtils.dpToPx(8F).toInt(), StipopUtils.dpToPx(8F).toInt())

    init {
        contentView = binding.root
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = Stipop.currentKeyboardHeight

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setIsClippedToScreen(true)
        } else {
            isClippingEnabled = false
        }

        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_FROM_FOCUSABLE

        applyTheme()

        with(binding) {
            packageThumbRecyclerView.run {
                setHasFixedSize(true)
                setItemViewCacheSize(20)
                adapter = packAdapter
            }
            stickerRecyclerView.run {
                addItemDecoration(decoration)
                setHasFixedSize(true)
                adapter = stickerAdapter
            }
            recentFavoriteContainer.setOnClickListener {
                onRecentFavoriteClick(!packAdapter.isSelectedItemExist())
            }
            storeImageView.setOnClickListener {
                showStore(0)
            }
        }
        ioScope.launch {
            keyboardViewModel.loadMyPackages().collectLatest {
                launch(Dispatchers.Main) {
                    packAdapter.submitData(it)
                }
            }
        }
        packAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                initialize(positionStart == 0)
                binding.packageThumbRecyclerView.scrollToPosition(0)
            }
        })

        itemTouchHelper =
            ItemTouchHelper(DragAndDropHelperCallback(packAdapter)).apply {
                attachToRecyclerView(binding.packageThumbRecyclerView)
            }
    }

    internal fun show(y: Int) {
        if (isShowing) {
            return
        }
        if (Stipop.currentKeyboardHeight > 0) {
            refreshData()
            showAtLocation(
                activity.window.decorView.findViewById(android.R.id.content) as View,
                Gravity.TOP,
                0,
                y
            )
            keyboardViewModel.trackSpv()
            spvPreview.spvTopCoordinate = height
            visibleDelegate.onSpvVisibleState(true)
        }
    }

    override fun dismiss() {
        if (isShowing) wantShowing = false
        spvPreview.dismiss()
        super.dismiss()
        packAdapter.updateSelected()
        visibleDelegate.onSpvVisibleState(false)
    }

    private fun refreshData() {
        getRecentFavorite(false)
        packAdapter.updateSelected()
        packAdapter.refresh()
    }

    private fun applyRecentFavoriteTheme() {
        with(binding) {
            recentFavoriteContainer.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            when (Config.showPreview) {
                true -> {
                    if (recentFavoriteContainer.tag == Constants.Tag.RECENT) {
                        smallRecently.setIconDefaultsColor40Opacity()
                        smallFavorite.setIconDefaultsColor()
                    } else {
                        smallRecently.setIconDefaultsColor()
                        smallFavorite.setIconDefaultsColor40Opacity()
                    }
                }
                false -> {
                    recentStickerImageView.setTint()
                }
            }
        }
    }

    private fun showStickers(selectedPackage: StickerPackage) {
        binding.emptyListTextView.isVisible = false
        binding.progressBar.isVisible = false
        StipopUtils.getStickersFromLocal(activity, selectedPackage.packageId).let { stickerList ->
            stickerAdapter.updateData(if (stickerList.isEmpty()) selectedPackage.stickers else stickerList)
            if (stickerList.isEmpty()) {
                StipopUtils.downloadAtLocal(selectedPackage)
            }
        }
    }

    private fun onRecentFavoriteClick(hasFocus: Boolean) {
        packAdapter.updateSelected()
        binding.progressBar.isVisible = true
        applyRecentFavoriteTheme()
        if (Config.showPreview) {
            if(hasFocus){
                if (binding.recentFavoriteContainer.tag == Constants.Tag.RECENT) {
                    binding.recentFavoriteContainer.tag = Constants.Tag.FAVORITE
                } else {
                    binding.recentFavoriteContainer.tag = Constants.Tag.RECENT
                }
            }
        } else {
            binding.recentFavoriteContainer.tag = Constants.Tag.RECENT
        }
        getRecentFavorite(true)
    }

    private fun getRecentFavorite(isClickedRequest: Boolean) {
        binding.progressBar.isVisible = false
        stickerAdapter.clearData()
        when (binding.recentFavoriteContainer.tag) {
            Constants.Tag.RECENT -> {
                keyboardViewModel.loadRecent(onSuccess = {
                    binding.progressBar.isVisible = false
                    if (it.isEmpty()) {
                        binding.emptyListTextView.isVisible = true
                        initialize(!isClickedRequest)
                    } else {
                        applyRecentFavoriteTheme()
                        it.forEach {
                            stickerAdapter.updateData(it)
                        }
                    }
                })
            }
            Constants.Tag.FAVORITE -> {
                keyboardViewModel.loadFavorites(onSuccess = {
                    binding.progressBar.isVisible = false
                    if (it.isEmpty()) {
                        initialize(!isClickedRequest)
                    } else {
                        applyRecentFavoriteTheme()
                        it.forEach {
                            stickerAdapter.updateData(it)
                        }
                    }
                })
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
            smallFavorite.setImageResource(R.mipmap.ic_favorites_active)
            smallRecently.setImageResource(R.mipmap.ic_recents_active)
            recentFavoriteContainer.tag = Constants.Tag.RECENT
            recentStickerImageView.isVisible = !Config.showPreview
            smallRecently.isVisible = Config.showPreview
            smallFavorite.isVisible = Config.showPreview
            stickerRecyclerView.layoutManager =
                GridLayoutManager(activity, Config.keyboardNumOfColumns)
        }
        applyRecentFavoriteTheme()
    }

    private fun initialize(isFirst: Boolean? = false) {
        if (isFirst == true) {
            if (keyboardViewModel.recentStickers.isEmpty() && !packAdapter.isSelectedItemExist()) {
                packAdapter.getItemByPosition(0)?.let {
                    onPackageClick(0, it)
                }
            }
        }
    }

    private fun sendSticker(spSticker: SPSticker) {
        Stipop.send(
            spSticker.stickerId,
            spSticker.keyword,
            Constants.Point.PICKER_VIEW
        ) { result ->
            if (result) {
                Stipop.instance?.delegate?.onStickerSelected(spSticker)
                keyboardViewModel.saveRecent(spSticker)
                spvPreview.dismiss()
            }
        }
    }

    override fun onPackageClick(position: Int, stickerPackage: StickerPackage) {
        with(binding) {
            emptyListTextView.isVisible = false
            progressBar.isVisible = true
            recentFavoriteContainer.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            recentStickerImageView.clearTint()
            smallRecently.clearTint()
            smallFavorite.clearTint()
        }
        packAdapter.updateSelected(position)
        stickerAdapter.clearData()
        keyboardViewModel.loadStickerPackage(stickerPackage, onSuccess = { showStickers(it) })
    }

    override fun onStickerClick(position: Int, spSticker: SPSticker) {
        if (Config.showPreview) {
            val isSame = spvPreview.showOrUpdate(spSticker)
            if (isSame) {
                sendSticker(spSticker)
            }
        } else {
            sendSticker(spSticker)
        }
    }

    override fun onPreviewFavoriteChanged(sticker: SPSticker) {
        stickerAdapter.updateFavorite(sticker)?.let {
            StipopUtils.saveStickerAsJson(activity, it)
        }
    }

    override fun onPreviewStickerClicked(sticker: SPSticker) {
        sendSticker(sticker)
    }

    override fun onItemClicked(packageId: Int, entrancePoint: String) {
        //
    }

    override fun onItemLongClicked(position: Int) {
        //
    }

    override fun onVisibilityClicked(wantToVisible: Boolean, packageId: Int, position: Int) {
        //
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onDragCompleted(fromData: Any, toData: Any) {
        keyboardViewModel.changePackageOrder(fromData as StickerPackage, toData as StickerPackage)
    }
}