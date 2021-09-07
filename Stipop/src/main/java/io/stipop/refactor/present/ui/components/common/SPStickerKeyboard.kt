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
        val presenter: Presenter?
        val isShow: Boolean

        fun onBind(presenter: Presenter?)
        fun onShow()
        fun onDismiss()
    }

    interface Presenter {
        fun willShow()
        fun didShow()
        fun willDismiss()
        fun didDismiss()
    }
}

class SPStickerKeyboardPresenter : SPStickerKeyboard.Presenter {

    init {
        Stipop.appComponent.inject(this)
    }

    @Inject
    lateinit var _viewModel: StickerKeyboardViewModel

    override fun willShow() {
        Log.d(this::class.simpleName, "willShow")
        _viewModel.onLoadMorePackageList(0)
    }

    override fun didShow() {
        Log.d(this::class.simpleName, "didShow")

    }

    override fun willDismiss() {
        Log.d(this::class.simpleName, "willDismiss")
    }

    override fun didDismiss() {
        Log.d(this::class.simpleName, "didDismiss")
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

    private var _presenter: SPStickerKeyboard.Presenter? = null

    override val presenter: SPStickerKeyboard.Presenter?
        get() = _presenter

    override fun onBind(presenter: SPStickerKeyboard.Presenter?) {
        _presenter = presenter
    }

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
                Log.d(this::class.simpleName, "packageList.size = ${it.size}")
                (_binding.myActivePackageList.adapter as? SPPaging.View<SPPackageItem>)?.setItemList(it)
            }

            stickerList.observe(_activity) {
                Log.d(this::class.simpleName, "stickerList.size = ${it.size}")
                (_binding.stickerList.adapter as? SPPaging.View<SPStickerItem>)?.setItemList(it)
            }
        }
    }

    private fun _setBinding() {
        _binding = LayoutKeyboardBinding.inflate(LayoutInflater.from(_activity)).apply {

            recentButton.setOnClickListener {
                _viewModel.onSelectPackage(null)
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
                }
            }
            stickerList.apply {
                layoutManager = GridLayoutManager(_activity, 3)
                adapter = KeyboardStickerAdapter().apply {
                    onBind(_stickerPagingPresenter)
                }
            }
        }
        contentView = _binding.root
    }


    override fun onShow() {
        Log.d(this::class.simpleName, "onShow")

        _rootView?.let {

            showAtLocation(it.rootView, Gravity.DISPLAY_CLIP_HORIZONTAL or Gravity.BOTTOM, 0, 0)
            update(0, 0, _keyboardWidth, _keyboardHeight)

            if (!_isShowKeyboard) {
                (_activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    0
                )

            }

            _viewModel.onLoadMorePackageList(-1)
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

    override fun onLoadMoreList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadMoreList : \n" +
                    "index -> $index"
        )
        _viewModel.onLoadMorePackageList(index)
    }

    override fun onClickedItem(item: SPPackageItem) {
        _viewModel.onSelectPackage(item)
    }
}

class KeyboardStickerPresenter: SPPaging.Presenter<SPStickerItem> {

    init {
        Stipop.appComponent.inject(this)
    }

    @Inject
    lateinit var _viewModel: StickerKeyboardViewModel

    override fun onLoadMoreList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadMoreList : \n" +
                    "index -> ${index}"
        )
    }

    override fun onClickedItem(item: SPStickerItem) {
        Log.d(
            this::class.simpleName, "onClickedItem : \n" +
                    "item -> $item"
        )
        _viewModel.onSelectSticker(item)
    }
}
