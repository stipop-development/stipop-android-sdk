package io.stipop

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

internal interface StipopEventListener {
    fun onStickerSelected(sticker: SPSticker): Boolean
    fun onPackageSelected(spPackage: SPPackage): Boolean
}

internal object StipopV2 {
    var isConfigured = false
    var isConnected = false
    var userId = "-1"
        private set
    lateinit var locale: Locale
        private set

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private val configRepository: ConfigRepository by lazy { ConfigRepository(StipopApi.create()) }
    private var stipopButton: StipopImageView? = null
    private var delegate: StipopEventListener? = null
    private var stickerIconEnabled = false
    private var keyboardHeight = 0
    private var keyboard: KeyboardPopup? = null

    fun configure(context: Context) {
        Config.configure(context)
        scope.launch {
            if (!isConfigured) {
                configRepository.postConfigSdk {
                    isConfigured = true
                }
            }
        }
    }

    fun connect(
        activity: Activity,
        userId: String,
        delegate: StipopEventListener,
        stipopButton: StipopImageView? = null,
        locale: Locale? = null,
    ) {
        connect(activity, userId, delegate, stipopButton, locale?.language, locale?.country)
    }

    fun connect(
        activity: Activity,
        userId: String,
        delegate: StipopEventListener,
        stipopButton: StipopImageView? = null,
        lang: String? = null,
        countryCode: String? = null,
    ) {
        this.userId = userId
        var locale = Locale.getDefault()
        if (lang != null && Locale.getISOLanguages().contains(lang)) {
            locale = Locale(lang, locale.country)
        }
        if (countryCode != null && Locale.getISOCountries().contains(countryCode)) {
            locale = Locale(locale.language, countryCode)
        }
        this.locale = locale
        val requestBody = InitSdkBody(userId = this.userId, language = this.locale.language)
        scope.launch {
            if (!isConnected) {
                configRepository.postInitSdk(requestBody, onSuccess = {
                    this@StipopV2.stipopButton = stipopButton
                    this@StipopV2.delegate = delegate
                    calculate(activity)
                    isConnected = true
                })
            } else {
                this@StipopV2.stipopButton = stipopButton
                this@StipopV2.delegate = delegate
                calculate(activity)
            }
        }
    }

    internal fun send(
        stickerId: Int,
        keyword: String,
        entrancePoint: String,
        completionHandler: (result: Boolean) -> Unit
    ) {
        if (isConnected) {
            scope.launch {
                configRepository.postTrackUsingSticker(
                    stickerId = stickerId.toString(),
                    userId = Stipop.userId,
                    query = keyword,
                    countryCode = Stipop.countryCode,
                    lang = Stipop.lang,
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

    fun showSearch(context: Context) {
        if (isConnected) {
            Intent(context, SearchActivity::class.java).run {
                context.startActivity(this)
            }
        }
    }

    fun showKeyboard(activity: Activity) {
        if (isConnected) {
            enableStickerIcon()
            keyboard = keyboard ?: KeyboardPopup(activity)
            keyboard?.let {
                if (it.isShowing) {
                    it.canShow = false
                    it.hide()
                    disableStickerIcon()
                } else {
                    if (keyboardHeight == 0) {
                        Utils.showKeyboard(activity)
                    }
                    it.canShow = true
                    it.show()
                }
            }
        }
    }

    fun hideKeyboard() {
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

    private fun calculate(activity: Activity) {
        stipopButton?.setImageResource(Config.getStickerIconResourceId(activity))
        stipopButton?.setIconDefaultsColor()
        val rootView = activity.window.decorView.findViewById(android.R.id.content) as View
        setSizeForSoftKeyboard(rootView)
    }

    private fun setSizeForSoftKeyboard(rootView: View) {
        var maxTop = 0
        var maxBottom = 0
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val visibleFrameSize = Rect()
            rootView.getWindowVisibleDisplayFrame(visibleFrameSize)
            val screenHeight = Utils.getScreenHeight(rootView.context)
            val visibleFrameHeight: Int = visibleFrameSize.bottom - visibleFrameSize.top
            var b = 0
            if (screenHeight < visibleFrameSize.bottom) {
                b = visibleFrameSize.bottom - screenHeight
            }
            if (b > maxBottom) {
                maxBottom = b
            }
            if (visibleFrameSize.top > maxTop) {
                maxTop = visibleFrameSize.top
            }
            val heightDifference = screenHeight - maxTop - visibleFrameHeight + maxBottom

            if (heightDifference > 100) {
                keyboardHeight = heightDifference
                keyboard?.let {
                    val preHeight = it.height
                    it.height = keyboardHeight
                    if (preHeight == 0 || !it.isShowing) {
                        it.show()
                        if (it.canShow) {
                            enableStickerIcon()
                        }
                    }
                }
            } else {
                keyboardHeight = 0
                keyboard?.let {
                    it.height = 0
                    it.hide()
                    disableStickerIcon()
                }
            }
        }
    }

    private fun enableStickerIcon() {
        if (isConnected) {
            stipopButton?.setTint()
            stickerIconEnabled = true
        }
    }

    private fun disableStickerIcon() {
        if (isConnected) {
            stipopButton?.clearTint()
        }
    }
}