package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.databinding.LayoutKeyboardBinding
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.present.ui.adapters.KeyboardPackageAdapter
import io.stipop.refactor.present.ui.adapters.KeyboardStickerAdapter
import io.stipop.refactor.present.ui.pages.store.SPStoreActivity
import io.stipop.refactor.present.ui.view_models.StickerKeyboardViewModel
import io.stipop.refactor.present.ui.view_models.StoreMode
import javax.inject.Inject

class SPStickerKeyboardPopupWindow(
    val _targetView: View,
    val _itemClick: ((SPStickerItem) -> Unit)?,

    ) : PopupWindow(
    SPStickerKeyboard(_targetView.context).also {
        it.itemClick = _itemClick
    },
    MATCH_PARENT,
    WRAP_CONTENT
) {

    init {
        _targetView.let {

            it.viewTreeObserver.addOnPreDrawListener {
                _keyboardHeight = if (_activityHeight > it.height) {
                    _activityHeight - it.height
                } else {
                    _keyboardHeight
                }

                true
            }

            it.viewTreeObserver.addOnGlobalLayoutListener {
                _isShowKeyboard = _activityHeight - it.height > 0

                if (!_isShowKeyboard) {
                    onDismiss()
                }
            }
        }

    }

    private val _metrics: DisplayMetrics
        get() {
            return Resources.getSystem().displayMetrics ?: DisplayMetrics()
        }

    private val _inputMethodManager: InputMethodManager? get() = (_targetView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)

    private val _activityHeight: Int = _metrics.heightPixels
    private val _activityWidth: Int = _metrics.widthPixels

    private var _isShowKeyboard: Boolean = false
    private var _keyboardHeight: Int = -1
    private val _keyboardWidth: Int get() = _activityWidth

    val isShow: Boolean
        get() = isShowing

    fun onShow() {
        Log.d(this::class.simpleName, "onShow")
        _targetView.let {

            it.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            showAsDropDown(it.rootView, 0, 0)

            if (!_isShowKeyboard) {
                _inputMethodManager?.run {
                    showSoftInput(_targetView.rootView, InputMethodManager.SHOW_FORCED)
                }
            }


        }
    }

    fun onDismiss() {
        Log.d(this::class.simpleName, "onDismiss")
        dismiss()
    }
}

class SPStickerKeyboard(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private lateinit var _binding: LayoutKeyboardBinding

    @Inject
    lateinit var _viewModel: StickerKeyboardViewModel

    var itemClick: ((SPStickerItem) -> Unit)? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        Stipop._appComponent.inject(this)

        (context as? AppCompatActivity)?.let {

            _viewModel.packageItemChanges.observe(it) {
                it.let {
                    onChangePackage(it)
                }
            }

            _viewModel.packageItemList.observe(it) {
                it?.let {
                    onChangePackageItemList(it)
                }
            }
            _viewModel.stickerItemList.observe(it) {
                it?.let {
                    onChangeStickerList(it)
                }
            }

            _viewModel.onSelectPackageItem(null)
            _viewModel.onLoadMorePackageItemList(-1)
        }

        _binding = LayoutKeyboardBinding.inflate(LayoutInflater.from(context), this, true).apply {

            recentButton.apply {
                setOnClickListener {
                    onSelectPackageItem(null)
                }
            }

            packageList.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).let {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            when (newState) {
                                RecyclerView.SCROLL_STATE_DRAGGING -> {
                                    _viewModel.onLoadMorePackageItemList(it.findLastCompletelyVisibleItemPosition())
                                }
                            }
                        }
                    })
                    it
                }
                adapter = KeyboardPackageAdapter().let {
                    it.itemClick = {
                        onSelectPackageItem(it)
                    }
                    it
                }
            }

            stickerList.apply {
                setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
                layoutManager = GridLayoutManager(context, Config.detailNumOfColumns).let {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            when (newState) {
                                RecyclerView.SCROLL_STATE_DRAGGING -> {
                                    _viewModel.onLoadMorePackageItemList(it.findLastCompletelyVisibleItemPosition())
                                }
                            }
                        }
                    })
                    it
                }
                adapter = KeyboardStickerAdapter().apply {
                    itemClick = {
                        onSelectStickerItem(it)
                    }
                }
            }

            settingButton.apply {
                setOnClickListener {
                    onShowMyPage()
                }
            }

            storeButton.apply {
                setOnClickListener {
                    onShowStorePage()
                }
            }
        }
    }

    private fun onChangePackage(item: SPPackageItem?) {
        Log.d(
            this::class.simpleName, "onChangePackage : \n" +
                    "item -> $item"
        )
        _binding.recentButton.isSelected = item == null
        (_binding.packageList.adapter as? KeyboardPackageAdapter)?.apply {
            selectedItem = item
            notifyDataSetChanged()
        }
    }

    private fun onSelectPackageItem(item: SPPackageItem?) {
        Log.d(
            this::class.simpleName, "onSelectPackageItem : \n" +
                    "item -> $item"
        )
        _viewModel.onSelectPackageItem(item)
    }

    private fun onSelectStickerItem(item: SPStickerItem) {
        Log.d(
            this::class.simpleName, "onSelectStickerItem : \n" +
                    "item -> $item"
        )
        itemClick?.invoke(item)
    }

    private fun onChangePackageItemList(itemList: List<SPPackageItem>) {
        Log.d(
            this::class.simpleName, "onChangePackageItemList : \n" +
                    "itemList.size -> ${itemList.size}"
        )
        (_binding.packageList.adapter as? KeyboardPackageAdapter)?.submitList(itemList)
    }

    private fun onChangeStickerList(itemList: List<SPStickerItem>) {
        Log.d(
            this::class.simpleName, "onChangeStickerList : \n" +
                    "itemList.size -> ${itemList.size}"
        )
        (_binding.stickerList.adapter as? KeyboardStickerAdapter)?.submitList(itemList)
    }

    private fun onShowStorePage() {
        Log.d(this::class.simpleName, "onShowStorePage")
        context.startActivity(Intent(context, SPStoreActivity::class.java).apply {
            putExtra(StoreMode.TAG, StoreMode.STORE_PAGE.rawValue)
        })
    }

    private fun onShowMyPage() {
        Log.d(this::class.simpleName, "onShowMyPage")
        context.startActivity(Intent(context, SPStoreActivity::class.java).apply {
            putExtra(StoreMode.TAG, StoreMode.MY_PAGE.rawValue)
        })
    }


}
