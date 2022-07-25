package io.stipop.view.pickerview

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
import io.stipop.Config
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.StipopUtils
import io.stipop.adapter.PagingMyPackAdapter
import io.stipop.adapter.StickerDefaultAdapter
import io.stipop.custom.DragAndDropHelperCallback
import io.stipop.custom.HorizontalDecoration
import io.stipop.databinding.ViewPickerBinding
import io.stipop.event.MyPackEventDelegate
import io.stipop.event.PreviewDelegate
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import io.stipop.models.enum.StipopApiEnum
import io.stipop.s_auth.*
import io.stipop.view.SpvPreview
import io.stipop.view.StoreActivity
import io.stipop.view.pickerview.listener.VisibleStateListener
import io.stipop.view.viewmodel.StickerPickerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class PickerViewType {
    CUSTOM, ON_KEYBOARD
}

internal class StickerPickerViewClass(
    val type: PickerViewType,
    val stickerPickerKeyboardView: StickerPickerKeyboardView? = null,
    val stickerPickerCustomFragment: StickerPickerCustomFragment? = null,
    val activity: Activity,
    val pickerView: ViewPickerBinding
): StickerDefaultAdapter.OnStickerClickListener,
    MyPackEventDelegate,
    SPVRecentStickerAdapterReRequestDelegate,
    SPVGetMyStickersReRequestDelegate,
    PreviewDelegate
{

    companion object {
        var spvRecentStickerAdapterReRequestDelegate: SPVRecentStickerAdapterReRequestDelegate? = null
        var spvGetMyStickersReRequestDelegate: SPVGetMyStickersReRequestDelegate? = null
    }

    var stickerPickerViewPreview: SpvPreview? = null

    private val stickerAdapter: StickerDefaultAdapter by lazy { StickerDefaultAdapter(this) }
    private val packAdapter: PagingMyPackAdapter by lazy { PagingMyPackAdapter(PagingMyPackAdapter.ViewType.SPV,this) }

    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)
    private var itemTouchHelper: ItemTouchHelper? = null
    private val decoration =HorizontalDecoration(StipopUtils.dpToPx(8F).toInt(), StipopUtils.dpToPx(8F).toInt())

    var delegate: VisibleStateListener? = null

    init {
        StickerPickerViewClass.spvRecentStickerAdapterReRequestDelegate = this
        StickerPickerViewClass.spvGetMyStickersReRequestDelegate = this
        when(type){
            PickerViewType.ON_KEYBOARD -> pickerKeyboardViewInit()
            PickerViewType.CUSTOM -> {}
        }

        commonInit()
    }

    private fun pickerKeyboardViewInit(){
        stickerPickerKeyboardView?.contentView = pickerView.root
        stickerPickerKeyboardView?.width = LinearLayout.LayoutParams.MATCH_PARENT
        stickerPickerKeyboardView?.height = Stipop.currentPickerViewHeight

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            stickerPickerKeyboardView?.setIsClippedToScreen(true)
        } else {
            stickerPickerKeyboardView?.isClippingEnabled = false
        }

        stickerPickerKeyboardView?.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        stickerPickerKeyboardView?.inputMethodMode = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE
    }

    private fun commonInit(){

        Stipop.stickerPickerViewModel = StickerPickerViewModel()
        Stipop.stickerPickerViewModel?.let {
            stickerPickerViewPreview = SpvPreview(activity, this, it)
        }
        applyTheme()

        with(pickerView) {
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
            Stipop.stickerPickerViewModel?.loadMyPackages()?.collectLatest {
                launch(Dispatchers.Main) {
                    packAdapter.submitData(it)
                }
            }
        }
        packAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                initialize(positionStart == 0)
                pickerView.packageThumbRecyclerView.scrollToPosition(0)
            }
        })

        itemTouchHelper =
            ItemTouchHelper(DragAndDropHelperCallback(packAdapter)).apply {
                attachToRecyclerView(pickerView.packageThumbRecyclerView)
            }

        pickerView.settingImageView.setOnClickListener{
            showStore(2)
        }
        pickerView.searchImageView.setOnClickListener {
            Stipop.showSearch()
        }
        when(Config.pickerViewSearchIsActive){
            true -> pickerView.searchImageView.visibility = View.VISIBLE
            false -> pickerView.searchImageView.visibility = View.GONE
        }
    }

    internal fun setDelegate(visibleDelegate: VisibleStateListener){
        this.delegate = visibleDelegate
    }

    internal fun applyTheme() {
        with(pickerView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.indeterminateTintList =
                    ColorStateList.valueOf(Color.parseColor(Config.themeMainColor))
            }
            containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            packageListHeader.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            settingImageView.setIconDefaultsColor()
            storeImageView.setImageResource(io.stipop.Config.getKeyboardStoreResourceId(activity))
            storeImageView.setIconDefaultsColor()
//            smallFavorite.setImageResource(R.mipmap.ic_favorites_active)
//            smallRecently.setImageResource(R.mipmap.ic_recents_active)
            recentFavoriteContainer.tag = io.stipop.Constants.Tag.RECENT
            recentStickerImageView.isVisible = !Config.showPreview
            smallRecently.isVisible = Config.showPreview
            smallFavorite.isVisible = Config.showPreview
            stickerRecyclerView.layoutManager =
                GridLayoutManager(activity, Config.keyboardNumOfColumns)
        }
        applyRecentFavoriteTheme()
    }

    private fun applyRecentFavoriteTheme() {
        with(pickerView) {
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

    private fun onRecentFavoriteClick(hasFocus: Boolean) {
        packAdapter.updateSelected()
        pickerView.progressBar.isVisible = true
        applyRecentFavoriteTheme()
        if (Config.showPreview) {
            if (hasFocus) {
                if (pickerView.recentFavoriteContainer.tag == Constants.Tag.RECENT) {
                    pickerView.recentFavoriteContainer.tag = Constants.Tag.FAVORITE
                } else {
                    pickerView.recentFavoriteContainer.tag = Constants.Tag.RECENT
                }
            }
        } else {
            pickerView.recentFavoriteContainer.tag = Constants.Tag.RECENT
        }
        getRecentFavorite(true)
    }

    private fun refreshData() {
        getRecentFavorite(false)
        packAdapterRefresh()
    }

    internal fun getRecentFavorite(isClickedRequest: Boolean) {
        pickerView.progressBar.isVisible = false
        stickerAdapter.clearData()
        when (pickerView.recentFavoriteContainer.tag) {
            Constants.Tag.RECENT -> {
                Stipop.stickerPickerViewModel?.loadRecent(
                    isClickedRequest = isClickedRequest,
                    onSuccess = {
                        pickerView.progressBar.isVisible = false
                        if (it.isEmpty()) {
                            pickerView.emptyListTextView.isVisible = true
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
                loadFavorites(isClickedRequest)
            }
        }
    }

    internal fun loadFavorites(isClickedRequest: Boolean){
        Stipop.stickerPickerViewModel?.loadFavorites(
            isClickedRequest = isClickedRequest,
            onSuccess = {
                pickerView.progressBar.isVisible = false
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

    internal fun packAdapterRefresh(){
        packAdapter.updateSelected()
        packAdapter.refresh()
    }
    private fun initialize(isFirst: Boolean? = false) {
        if (isFirst == true) {
            Stipop.stickerPickerViewModel?.let {
                if (it.recentStickers.isEmpty() && !packAdapter.isSelectedItemExist()) {
                    packAdapter.getItemByPosition(0)?.let {
                        when (type) {
                            PickerViewType.CUSTOM -> {}
                            PickerViewType.ON_KEYBOARD -> onPackageClick(0, it)
                        }
                    }
                }
            }
        }
    }

    internal fun show(y: Int = 0){
        when(type){
            PickerViewType.ON_KEYBOARD -> showPickerKeyboardView(y)
            PickerViewType.CUSTOM -> showPickerCustomView()
        }
    }

    private fun showPickerKeyboardView(y: Int){
        if (stickerPickerKeyboardView?.isShowing == true) {
            return
        }
        if (Stipop.currentPickerViewHeight > 0) {
            try {
                showPickerViewCommonFunction()
                stickerPickerKeyboardView?.showAtLocation(
                    activity.window.decorView.findViewById(android.R.id.content) as View,
                    Gravity.TOP,
                    0,
                    y
                )
                stickerPickerViewPreview?.spvTopCoordinate = stickerPickerKeyboardView?.height ?: 0
            } catch(exception: Exception){

            }
        }
    }

    private fun showPickerCustomView(){
        if(stickerPickerCustomFragment?.isShowing() == true){
            return
        }

        showPickerViewCommonFunction()
        setPickerCustomViewVisibility(true)
    }

    private fun showPickerViewCommonFunction(){
        refreshData()
        Stipop.stickerPickerViewModel?.trackSpv()
        delegate?.onSpvVisibleState(true)
    }

    private fun setPickerCustomViewVisibility(visibilityBool: Boolean){
        when(visibilityBool){
            true -> pickerView.containerLL.visibility = View.VISIBLE
            false -> pickerView.containerLL.visibility = View.GONE
        }
    }

    internal fun dismiss(){
        when(type){
            PickerViewType.ON_KEYBOARD -> {
                stickerPickerKeyboardView?.wantShowing = false
                dismissCommonFunction()
            }
            PickerViewType.CUSTOM -> {
                setPickerCustomViewVisibility(false)
                dismissCommonFunction()
            }
        }
    }

    private fun dismissCommonFunction(){
        stickerPickerViewPreview?.dismiss()
        packAdapter.updateSelected()
        delegate?.onSpvVisibleState(false)
    }

    private fun showStickers(selectedPackage: StickerPackage) {
        pickerView.emptyListTextView.isVisible = false
        pickerView.progressBar.isVisible = false
        StipopUtils.getStickersFromLocal(activity, selectedPackage.packageId).let { stickerList ->
            stickerAdapter.updateData(if (stickerList.isEmpty()) selectedPackage.stickers else stickerList)
            if (stickerList.isEmpty()) {
                StipopUtils.downloadAtLocal(selectedPackage)
            }
        }
    }

    private fun sendSticker(spSticker: SPSticker) {
        Stipop.send(
            TrackUsingStickerEnum.STICKER_PICKER_VIEW_CLASS_SINGLE_TAP,
            spSticker,
            Constants.Point.PICKER_VIEW
        ) { result ->
            if (result) {
                Stipop.instance?.delegate?.onStickerSingleTapped(spSticker)
                Stipop.stickerPickerViewModel?.saveRecent(spSticker)
                stickerPickerViewPreview?.dismiss()
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

    override fun onStickerSingleTap(position: Int, spSticker: SPSticker) {
        if (Config.showPreview) {
            stickerPickerViewPreview?.let {
                val isSame = it.showOrUpdate(spSticker)
                if (isSame) {
                    sendSticker(spSticker)
                }
            }
        } else {
            sendSticker(spSticker)
        }
    }

    override fun onStickerDoubleTap(position: Int, spSticker: SPSticker) {
        Stipop.send(
            TrackUsingStickerEnum.STICKER_PICKER_VIEW_CLASS_DOUBLE_TAP,
            spSticker,
            Constants.Point.SEARCH_VIEW
        ) { result ->
            if (result) {
                Stipop.instance?.delegate?.onStickerDoubleTapped(spSticker)
                dismiss()
            }
        }
    }

    override fun onPackageClick(position: Int, stickerPackage: StickerPackage) {
        with(pickerView) {
            emptyListTextView.isVisible = false
            progressBar.isVisible = true
            recentFavoriteContainer.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            recentStickerImageView.clearTint()
            smallRecently.clearTint()
            smallFavorite.clearTint()
        }
        packAdapter.updateSelected(position)
        stickerAdapter.clearData()
        loadStickerPackage(stickerPackage)
    }

    internal fun loadStickerPackage(stickerPackage: StickerPackage){
        Stipop.stickerPickerViewModel?.loadStickerPackage(stickerPackage, onSuccess = {
            showStickers(it)
        })
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
        itemTouchHelper?.startDrag(viewHolder)
    }

    override fun onDragCompleted(fromData: Any, toData: Any) {
        Stipop.stickerPickerViewModel?.changePackageOrder(fromData as StickerPackage, toData as StickerPackage)
    }

    override fun onPreviewFavoriteChanged(sticker: SPSticker) {
        stickerAdapter.updateFavorite(sticker)?.let {
            StipopUtils.saveStickerAsJson(activity, it)
        }
    }

    override fun onPreviewStickerClicked(sticker: SPSticker) {
        sendSticker(sticker)
    }

    override fun recentStickerAdapterRetry() {
        //
    }

    override fun getMyStickersRetry() {
        packAdapter.retry()
    }
}