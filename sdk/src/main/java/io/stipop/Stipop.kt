package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.View
import io.stipop.api.StipopApi
import io.stipop.custom.StipopImageView
import io.stipop.data.ConfigRepository
import io.stipop.models.SPPackage
import io.stipop.models.SPSticker
import io.stipop.models.body.InitSdkBody
import io.stipop.view.KeyboardPopup
import io.stipop.view.SearchActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

interface StipopDelegate {
    fun onStickerSelected(sticker: SPSticker): Boolean
    fun canDownload(spPackage: SPPackage): Boolean
}

class Stipop(
    private val activity: Activity,
    private val stipopButton: StipopImageView,
    val delegate: StipopDelegate
) {

    companion object {

        private val scope = CoroutineScope(Job() + Dispatchers.Main)

        private val configRepository: ConfigRepository by lazy { ConfigRepository(StipopApi.create()) }

        lateinit var applicationContext: Context

        @SuppressLint("StaticFieldLeak")
        var instance: Stipop? = null

        var userId = "-1"
            private set

        var lang = "en"
            private set

        var countryCode = "us"
            private set

        var keyboardHeight = 0
            private set

        fun configure(context: Context) {
            Config.configure(context)
            scope.launch { configRepository.postConfigSdk() }
        }

        fun connect(
            activity: Activity,
            stipopButton: StipopImageView,
            userId: String,
            locale: Locale,
            delegate: StipopDelegate
        ) {
            connect(activity, stipopButton, userId, locale.language, locale.country, delegate)
        }

        fun connect(
            activity: Activity,
            stipopButton: StipopImageView,
            userId: String,
            lang: String,
            countryCode: String,
            delegate: StipopDelegate
        ) {
            Stipop.userId = userId
            Stipop.lang = lang
            Stipop.countryCode = countryCode

            val requestBody = InitSdkBody(userId = Stipop.userId, language = Stipop.lang)
            scope.launch {
                configRepository.postInitSdk(requestBody, onSuccess = {
                    Stipop(activity, stipopButton, delegate).apply {
                        connect()
                        instance = this
                        applicationContext = activity.applicationContext
                    }
                })
            }
        }

        fun showSearch() = instance?.showSearch()

        fun showKeyboard() = instance?.showKeyboard()

        fun hideKeyboard() = instance?.hideKeyboard()

        fun send(
            stickerId: Int,
            keyword: String,
            entrancePoint: String,
            completionHandler: (result: Boolean) -> Unit
        ) {
            if (instance == null || instance?.isConnected == false) {
                return
            }
            scope.launch {
                configRepository.postTrackUsingSticker(
                    stickerId = stickerId.toString(),
                    userId = userId,
                    query = keyword,
                    countryCode = countryCode,
                    lang = lang,
                    eventPoint = entrancePoint,
                    onSuccess = {
                        if (it.header.isSuccess()) {
                            completionHandler(true)
                        } else {
                            completionHandler(false)
                        }
                    })
            }
        }
    }

    private var maxTop = 0
    private var maxBottom = 0
    private var keyboard: KeyboardPopup? = null
    private lateinit var rootView: View


    private var isConnected = false
    private var stickerIconEnabled = false

    private fun connect() {
        this.stipopButton.setImageResource(Config.getStickerIconResourceId(this.activity))
        this.stipopButton.setIconDefaultsColor()
        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View
        this.setSizeForSoftKeyboard()
        this.isConnected = true
    }

    private fun enableStickerIcon() {
        if (this.isConnected) {
            this.stipopButton.setTint()
            this.stickerIconEnabled = true
        }
    }

    private fun disableStickerIcon() {
        if (this.isConnected) {
            this.stipopButton.clearTint()
        }
    }

    private fun showSearch() {
        if (!this.isConnected) {
            return
        }

        // this.enableStickerIcon()

        val intent = Intent(this.activity, SearchActivity::class.java)
        this.activity.startActivity(intent)
    }

    private fun showKeyboard() {
        if (!this.isConnected) {
            return
        }

        this.enableStickerIcon()

        if (keyboard == null) {
            keyboard = KeyboardPopup(activity)
        }

        if (keyboard!!.isShowing) {
            this.keyboard!!.canShow = false
            keyboard!!.hide()
            this.disableStickerIcon()
        } else {
            if (Stipop.keyboardHeight == 0) {
                Utils.showKeyboard(instance!!.activity)
            }
            this.keyboard!!.canShow = true
            keyboard!!.show()
        }
    }

    private fun hideKeyboard() {
        if (!this.isConnected) {
            return
        }
        keyboard?.let {
            if (it.isShowing) {
                it.canShow = false
                it.hide()
                disableStickerIcon()
            }
        }
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

                if (this.keyboard != null) {
                    val preHeight = this.keyboard!!.height
                    this.keyboard!!.height = keyboardHeight

                    if (preHeight == 0 || !this.keyboard!!.isShowing) {
                        this.keyboard!!.show()
                        if (this.keyboard!!.canShow) {
                            this.enableStickerIcon()
                        }
                    }
                }
            } else {
                keyboardHeight = 0
                if (this.keyboard != null) {
                    this.keyboard!!.height = 0
                    this.keyboard!!.hide()
                    this.disableStickerIcon()
                }
            }
        }
    }

}