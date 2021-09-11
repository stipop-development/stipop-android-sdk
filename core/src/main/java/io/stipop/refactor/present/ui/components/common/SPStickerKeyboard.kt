package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
import io.stipop.refactor.present.ui.contracts.StipopContract
import io.stipop.refactor.present.ui.listeners.OnItemSelectListener
import io.stipop.refactor.present.ui.pages.store.SPStoreActivity
import io.stipop.refactor.present.ui.view_models.StickerKeyboardViewModel
import io.stipop.refactor.present.ui.view_models.StoreMode
import javax.inject.Inject

class SPStickerKeyboardPopupWindow(
    val _targetView: View,
) : PopupWindow(SPStickerKeyboard(_targetView.context)), StipopContract.View {

    init {
        _targetView.let {
            it.viewTreeObserver.addOnPreDrawListener {
                _keyboardHeight = if (_activityHeight > it.height) {
                    _activityHeight - it.height
                } else {
                    _keyboardHeight
                }

                _isShowKeyboard = _activityHeight - it.height > 0

                true
            }
            it.viewTreeObserver.addOnDrawListener {
                Log.d("HELLO", "_activityHeight -> $_activityHeight")
                Log.d("HELLO", "_keyboardHeight -> $_keyboardHeight")
                if (_isShowKeyboard) {
                    update(0, 0, _keyboardWidth, _keyboardHeight)
                } else {
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

    override val isShow: Boolean
        get() = isShowing

    private var _presenter: StipopContract.Presenter? = null
    override val presenter: StipopContract.Presenter?
        get() = _presenter

    override fun onShow() {
        Log.d(this::class.simpleName, "onShow")
        _targetView.let {
            showAtLocation(it, Gravity.BOTTOM, 0, 0)

            if (!_isShowKeyboard) {
                _inputMethodManager?.run {
                    showSoftInput(_targetView.rootView, InputMethodManager.SHOW_FORCED).run {
                        update(0, 0, _keyboardWidth, _keyboardHeight)
                    }
                }
            }
        }

    }

    //    https://messenger.stipop.io/v1/mysticker/0000?limit=20&pageNumber=1
    override fun onDismiss() {
        Log.d(this::class.simpleName, "onDismiss")
        dismiss()
    }
}

class SPStickerKeyboard(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private lateinit var _binding: LayoutKeyboardBinding

    @Inject
    lateinit var _viewModel: StickerKeyboardViewModel

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        Stipop._appComponent.inject(this)

        (context as? AppCompatActivity)?.let {

            _viewModel.selectedPackage.observe(it) {
                it.let {
                    onChangePackage(it)
                }
            }

            _viewModel.packageList.observe(it) {
                it?.let {
                    onChangePackageList(it)
                }
            }
            _viewModel.stickerList.observe(it) {
                it?.let {
                    onChangeStickerList(it)
                }
            }

            _viewModel.onLoadMorePackageList(-1)
        }

        _binding = LayoutKeyboardBinding.inflate(LayoutInflater.from(context), this, true).apply {

            recentButton.apply {
                setOnClickListener {
                    onSelectPackage(null)
                }
            }

            packageList.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).let {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            when (newState) {
                                RecyclerView.SCROLL_STATE_DRAGGING -> {
                                    _viewModel.onLoadMorePackageList(it.findLastCompletelyVisibleItemPosition())
                                }
                            }
                        }
                    })
                    it
                }
                adapter = KeyboardPackageAdapter().let {
                    it.onItemSelectListener = object : OnItemSelectListener<SPPackageItem> {
                        override fun onSelect(item: SPPackageItem) {
                            onSelectPackage(item)
                        }
                    }
                    it
                }
            }

            stickerList.apply {
                layoutManager = GridLayoutManager(context, Config.detailNumOfColumns).let {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            when (newState) {
                                RecyclerView.SCROLL_STATE_DRAGGING -> {
                                    _viewModel.onLoadMorePackageList(it.findLastCompletelyVisibleItemPosition())
                                }
                            }
                        }
                    })
                    it
                }
                adapter = KeyboardStickerAdapter().let {
                    it.onItemSelectListener = object : OnItemSelectListener<SPStickerItem> {
                        override fun onSelect(item: SPStickerItem) {
                            onSelectSticker(item)
                        }
                    }
                    it
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

    private fun onSelectPackage(item: SPPackageItem?) {
        Log.d(
            this::class.simpleName, "onSelectPackage : \n" +
                    "item -> $item"
        )
        _viewModel.onSelectPackage(item)
    }

    private fun onSelectSticker(item: SPStickerItem) {
        Log.d(
            this::class.simpleName, "onSelectSticker : \n" +
                    "item -> $item"
        )
        _viewModel.onSelectSticker(item)
    }

    private fun onChangePackageList(itemList: List<SPPackageItem>) {
        Log.d(
            this::class.simpleName, "onChangePackageList : \n" +
                    "itemList.size -> ${itemList.size}"
        )
        (_binding.packageList.adapter as? KeyboardPackageAdapter)?.let {
            it.itemList = itemList
            it.notifyDataSetChanged()
        }
    }

    private fun onChangeStickerList(itemList: List<SPStickerItem>) {
        Log.d(
            this::class.simpleName, "onChangeStickerList : \n" +
                    "itemList.size -> ${itemList.size}"
        )
        (_binding.stickerList.adapter as? KeyboardStickerAdapter)?.let {
            it.itemList = itemList
            it.notifyDataSetChanged()
        }
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
