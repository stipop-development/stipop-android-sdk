package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.View
import io.stipop.refactor.present.ui.pages.search_sticker.SPSearchStickerActivity
import io.stipop.extend.StipopImageView
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.data.models.SPUser
import io.stipop.refactor.data.repositories.UserRepository
import io.stipop.refactor.present.di.ApplicationComponent
import io.stipop.refactor.present.di.DaggerApplicationComponent
import io.stipop.refactor.present.ui.components.common.SPKeyboard
import io.stipop.refactor.present.ui.components.common.SPKeyboardFragment
import io.stipop.refactor.present.ui.components.common.SPKeyboardPopupWindow
import io.stipop.refactor.present.ui.components.common.SPKeyboardPresenter
import javax.inject.Inject

interface StipopDelegate {
    fun onStickerSelected(sticker: SPSticker): Boolean
    fun canDownload(spPackage: SPPackage): Boolean
}

class Stipop(private val activity: Activity, private val stipopButton: StipopImageView, val delegate: StipopDelegate) {
    @Inject
    internal lateinit var userRepository: UserRepository

    companion object {

        @SuppressLint("StaticFieldLeak")
        var instance: Stipop? = null

        internal val appComponent: ApplicationComponent = DaggerApplicationComponent.create()

        var keyboardHeight = 0
            private set

        fun configure(context:Context) {
            Config.configure(context)
        }

        fun connect(activity: Activity, stipopButton: StipopImageView, userId: String, lang: String, countryCode: String, delegate: StipopDelegate) {


            instance = Stipop(activity, stipopButton, delegate)


            instance?.connect()
            instance?.userRepository?.setUser(SPUser(userId, countryCode, lang, Config.apikey))

        }

        fun showSearch() {
            if (instance == null) {
                return
            }

            instance?.showSearch()
        }

        fun onToggleKeyboard() {
            if (instance == null) {
                return
            }


            instance?.onToggleKeyboard()
        }
    }

    private var maxTop = 0
    private var maxBottom = 0
    private var keyboardFragment: SPKeyboardFragment? = null
    private var _spKeyboardPresenter: SPKeyboard.Presenter? = null

    private lateinit var rootView: View


    private var connected = false
    private var stickerIconEnabled = false

    fun connect() {
        Log.d(this::class.simpleName, "connect")

        appComponent.inject(this)

        this.stipopButton.setImageResource(Config.getStickerIconResourceId(this.activity))

        this.connected = true

        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View

        this.setSizeForSoftKeyboard()

        _spKeyboardPresenter = SPKeyboardPresenter()
        _spKeyboardPresenter?.setView(SPKeyboardPopupWindow(this.activity))
    }

    private fun showSearch() {
        Log.d(this::class.simpleName, "showSearch")
        if (!this.connected) {
            return
        }

        // this.enableStickerIcon()

        val intent = Intent(this.activity, SPSearchStickerActivity::class.java)
        this.activity.startActivity(intent)
    }

    private fun onToggleKeyboard() {
        Log.d(this::class.simpleName, "showKeyboard")
        if (!this.connected) {
            return
        }

        _spKeyboardPresenter?.let {
            if (it.isShow) {
                it.onDismiss()
            } else {
                it.onShow()
            }
        }

//        this.enableStickerIcon()
//
//        if (keyboardFragment == null) {
//            keyboardFragment = SPKeyboardFragment()
//        }
//
//        keyboardFragment?.let {
//
//                keyboardFragment ->
//
//            if (keyboardFragment.popupWindow?.isShowing) {
//                this.keyboardFragment?.canShow = false
//                keyboardFragment.hide()
//                this.disableStickerIcon()
//            } else {
//                if (Stipop.keyboardHeight == 0) {
//                    Utils.showKeyboard(instance?.activity)
//                }
//
//                this.keyboardFragment?.canShow = true
//                keyboardFragment.show()
//            }
//        }
    }

    private fun setSizeForSoftKeyboard() {

        this.rootView.viewTreeObserver.addOnGlobalLayoutListener {

            val visibleFrameSize = Rect()
            this.rootView.getWindowVisibleDisplayFrame(visibleFrameSize)

            val screenHeight = Utils.getScreenHeight(this.activity)
            val visibleFrameHeight: Int = visibleFrameSize.bottom - visibleFrameSize.top

            var b = 0
            if (screenHeight < visibleFrameSize.bottom) {
                b = visibleFrameSize.bottom - screenHeight
            }

            if (b > this.maxBottom) {
                this.maxBottom = b
            }

            if (visibleFrameSize.top > this.maxTop) {
                this.maxTop = visibleFrameSize.top
            }

            val heightDifference = screenHeight - this.maxTop - visibleFrameHeight + this.maxBottom

            if (heightDifference > 100) {
                keyboardHeight = heightDifference

                this.keyboardFragment?.let { keyboard ->
//                    keyboard.popupWindow.let { popupWindow ->
//                        val preHeight = popupWindow.height
//                        popupWindow.height = keyboardHeight
//
//                        if (preHeight == 0 || popupWindow.isShowing) {
//                            keyboard.show()
//                            if (keyboard.canShow) {
//                                this.enableStickerIcon()
//                            }
//                        }
//                    }
                }
            } else {
//                keyboardHeight = 0
//                this.keyboardFragment?.popupWindow?.height = 0
//                this.keyboardFragment?.hide()
//                this.disableStickerIcon()
            }
        }
    }

}
