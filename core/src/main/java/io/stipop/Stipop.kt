package io.stipop

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.stipop.extend.StipopImageView
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.present.di.ApplicationComponent
import io.stipop.refactor.present.di.DaggerApplicationComponent
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboardPopupWindow
import io.stipop.refactor.present.ui.components.common.SPStickerPreviewPopupWindow
import io.stipop.refactor.present.ui.pages.search_sticker.SPSearchStickerActivity
import io.stipop.refactor.present.ui.view_models.StipopViewModel
import org.json.JSONObject
import javax.inject.Inject

interface StipopDelegate {
    fun onStickerSelected(sticker: SPSticker): Boolean
    fun canDownload(spPackage: SPPackage): Boolean
}

class Stipop {
    companion object {

        val TAG: String? = this::class.simpleName

        var instance: Stipop? = null

        internal val _appComponent: ApplicationComponent = DaggerApplicationComponent.create()

        fun configure(context: Context) {
            Config.configure(context)
        }

        fun connect(
            activity: AppCompatActivity,
            stipopButton: StipopImageView,
            userId: String,
            lang: String,
            countryCode: String,
            delegate: StipopDelegate
        ) {
            instance = Stipop()
            instance?.connect(activity, stipopButton, userId, lang, countryCode, delegate)
        }

        fun showSearch() {
            instance?.showSearch()
        }

        fun showKeyboard() {
            instance?.showKeyboard()
        }

    }

    private val _metrics: DisplayMetrics
        get() {
            return Resources.getSystem().displayMetrics ?: DisplayMetrics()
        }

    private var _searchStickerIntent: Intent? = null
    private var _searchStickerLauncher: ActivityResultLauncher<Intent>? = null
    private var _stickerKeyboardPopupWindow: SPStickerKeyboardPopupWindow? = null
    private var _stickerPreviewPopupWindow: SPStickerPreviewPopupWindow? = null

    @Inject
    internal lateinit var _userRepository: UserRepository

    @Inject
    internal lateinit var _viewModel: StipopViewModel

    private var _delegate: StipopDelegate? = null

    fun connect(activity: AppCompatActivity, stipopButton: StipopImageView, userId: String, lang: String, countryCode: String, delegate: StipopDelegate) {
        Log.d(this::class.simpleName, "connect")

        _appComponent.inject(this)

        _userRepository.setUser(SPUser(userId, countryCode, lang, Config.apikey))

        _delegate = delegate

        activity.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {

                _viewModel.run {
                    selectStickerChanges.observe(activity) {
                        Log.d(TAG, "it -> $it")
                        onChangeStickerItem(it)
                    }
                }

                activity.window.decorView.findViewById<View>(android.R.id.content)?.let {
                    _stickerKeyboardPopupWindow =
                        SPStickerKeyboardPopupWindow(it) {
                            _viewModel.onSelectStickerItem(it)
                        }

                    _stickerPreviewPopupWindow = SPStickerPreviewPopupWindow(it)

                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                _stickerKeyboardPopupWindow = null
                _stickerPreviewPopupWindow = null
            }
        })

        _searchStickerIntent = Intent(activity, SPSearchStickerActivity::class.java)
        _searchStickerLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            it?.let {
                when (it.resultCode) {
                    SPSearchStickerActivity.Companion.Request.OK.rawValue -> {
                        it.data?.getStringExtra(SPSearchStickerActivity.Companion.Request.TAG)?.let {
                            Log.d(this::class.simpleName, "${SPSearchStickerActivity.Companion.Request.TAG} -> $it")
                            try {

                                JSONObject(it).let {
                                    _viewModel.onSelectStickerItem(
                                        SPStickerItem(
                                            it.getString("stickerImg"),
                                            it.getString("keyword"),
                                            it.getInt("stickerId"),
                                        )
                                    )
//                                    delegate.onStickerSelected(SPSticker(it))
                                }

                            } catch (e: Exception) {
                                Log.e(TAG, e.message, e)
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun onChangeStickerItem(it: SPStickerItem?) {

        it?.let {
            if (Config.showPreview) {
                showStickerItemPreview(it)
            } else {
                _delegate?.onStickerSelected(SPSticker(JSONObject().apply {
                    put("stickerId", it.stickerId)
                    put("stickerImg", it.stickerImg)
                    put("keyword", it.keyword)
                }))
            }
        }

    }

    private fun showSearch() {
        Log.d(this::class.simpleName, "showSearch")
        _searchStickerIntent?.let {
            _searchStickerLauncher?.launch(it)
        }
    }

    private fun showKeyboard() {
        Log.d(this::class.simpleName, "showKeyboard")
        _stickerKeyboardPopupWindow?.run {
            if (isShow) {
                onDismiss()
            } else {
                onShow()
            }
        }
    }

    private fun showStickerItemPreview(item: SPStickerItem?) {
        Log.d(TAG, "showStickerItemPreview : \n" +
                "item -> $item")

        if (item == null) {
            _stickerPreviewPopupWindow?.dismiss()
        } else {
            _stickerPreviewPopupWindow?.sticker = item
            _stickerPreviewPopupWindow?.show()
        }
    }
}
