package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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


interface SPStickerKeyboard {

    interface View {
        val isShow: Boolean
        fun onShow()
        fun onDismiss()
    }

    interface Presenter {
        val isShow: Boolean
        fun onShow()
        fun onDismiss()
        fun setView(view: View?)
    }
}

class SPStickerKeyboardPresenter : SPStickerKeyboard.Presenter {
    private var _view: SPStickerKeyboard.View? = null

    override val isShow: Boolean get() = _view?.isShow ?: false

    override fun onShow() {
        Log.d(this::class.simpleName, "onShow")
        _view?.onShow()
    }

    override fun onDismiss() {
        Log.d(this::class.simpleName, "onDismiss")
        _view?.onDismiss()
    }

    override fun setView(view: SPStickerKeyboard.View?) {
        Log.d(this::class.simpleName, "setView")
        _view = view
    }
}

class SPStickerKeyboardPopupWindow(
    private val _activity: AppCompatActivity,
) : PopupWindow(), SPStickerKeyboard.View {

    private var _rootView: View? = null

    private lateinit var _binding: LayoutKeyboardBinding

    @Inject
    internal lateinit var _viewModel: StickerKeyboardViewModel

    private var _packagePagingPresenter: SPPaging.Presenter<SPPackageItem>? = null
    private var _stickerPagingPresenter: SPPaging.Presenter<SPStickerItem>? = null

    private val _metrics: DisplayMetrics
        get() {
            return Resources.getSystem().displayMetrics ?: DisplayMetrics()
        }

    private val _activityHeight: Int get() = _metrics.heightPixels
    private val _activityWidth: Int get() = _metrics.widthPixels

    private var _isShowKeyboard = false
    private var _keyboardHeight: Int = -1
    private val _keyboardWidth: Int get() = _activityWidth

    override val isShow: Boolean
        get() = isShowing

    init {

        Stipop.appComponent.inject(this)

        _packagePagingPresenter = KeyboardPackagePresenter()

        _stickerPagingPresenter = KeyboardStickerPresenter()

        _setBinding()
        _setViewModel()

        _rootView = with(_activity.window.decorView.findViewById<View?>(android.R.id.content)) {
            this
        }
        _rootView?.let {

            it.viewTreeObserver.addOnPreDrawListener {
                _keyboardHeight = if (_activityHeight > it.height) {
                    _activityHeight - it.height
                } else {
                    _keyboardHeight
                }
                _isShowKeyboard = _activityHeight > it.height

                true
            }

            it.viewTreeObserver.addOnDrawListener {
                if (_isShowKeyboard) {
                    update(0, 0, _keyboardWidth, _keyboardHeight)
                } else {
                    onDismiss()
                }
            }
        }
    }

    private fun _setViewModel() {
        _viewModel.run {
            packageList.observe(_activity) {
                _packagePagingPresenter?.setItemList(it)
            }
        }
    }

    private fun _setBinding() {
        _binding = LayoutKeyboardBinding.inflate(LayoutInflater.from(_activity)).apply {

            recentButton.setOnClickListener {
                it?.isSelected = true
                _viewModel.onLoadMoreRecentlyStickerList(0)
            }

            settingButton.setOnClickListener {
                _activity.startActivity(Intent(_activity, SPStoreActivity::class.java).apply {
                    putExtra(StoreMode.TAG, StoreMode.MY_PAGE.rawValue)
                })
            }

            storeButton.setOnClickListener {
                _activity.startActivity(Intent(_activity, SPStoreActivity::class.java).apply {
                    putExtra(StoreMode.TAG, StoreMode.STORE_PAGE.rawValue)
                })
            }

            myActivePackageList.apply {
                layoutManager = LinearLayoutManager(_activity, RecyclerView.HORIZONTAL, false)
                adapter = KeyboardPackageAdapter().apply {
                    onBind(_packagePagingPresenter)
                    _packagePagingPresenter?.onBind(this)
                }
            }
            stickerList.apply {
                layoutManager = GridLayoutManager(_activity, 3)
                adapter = KeyboardStickerAdapter().apply {
                    onBind(_stickerPagingPresenter)
                    _stickerPagingPresenter?.onBind(this)
                }
            }
        }
        contentView = _binding.root
    }


    override fun onShow() {
        Log.d(this::class.simpleName, "onShow")

        _rootView?.let {
            Log.d(this::class.simpleName, "showAtLocation")

            _viewModel.onLoadMorePackageList(0)

            showAtLocation(it.rootView, Gravity.DISPLAY_CLIP_HORIZONTAL or Gravity.BOTTOM, 0, 0)
            update(0, 0, _keyboardWidth, _keyboardHeight)

            if (!_isShowKeyboard) {
                    (_activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(
                        InputMethodManager.SHOW_FORCED,
                        0
                    )

            }

        }
    }

    override fun onDismiss() {
        Log.d(this::class.simpleName, "onDismiss")
        dismiss()
    }
}

class KeyboardPackagePresenter : SPPaging.Presenter<SPPackageItem> {

    init {
        Stipop.appComponent.inject(this)
    }

    @Inject
    lateinit var _viewModel: StickerKeyboardViewModel

    var _view: SPPaging.View<SPPackageItem>? = null

    override fun onBind(view: SPPaging.View<SPPackageItem>?) {
        Log.d(this::class.simpleName, "onBind : \n" +
                "view -> $view")
        _view = view
    }

    override fun onLoadMoreList(index: Int) {
        Log.d(this::class.simpleName, "onLoadMoreList : \n" +
                "index -> $index")

        _viewModel.onLoadMorePackageList(index)
    }

    override val view: SPPaging.View<SPPackageItem>?
        get() = _view

    override fun setItemList(itemList: List<SPPackageItem>) {
        Log.d(this::class.simpleName, "setItemList : \n" +
                "itemList.size -> ${itemList.size}")
        view?.setItemList(itemList)
    }
}

class KeyboardStickerPresenter: SPPaging.Presenter<SPStickerItem> {

    @Inject
    lateinit var _viewModel: StickerKeyboardViewModel

    var _view: SPPaging.View<SPStickerItem>? = null

    override fun onBind(view: SPPaging.View<SPStickerItem>?) {
        _view = view
    }

    override fun onLoadMoreList(index: Int) {
    }

    override val view: SPPaging.View<SPStickerItem>?
        get() = _view

    override fun setItemList(itemList: List<SPStickerItem>) {

    }
}
