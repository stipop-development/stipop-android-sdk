package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import io.stipop.api.StipopApi
import io.stipop.custom.StipopImageView
import io.stipop.data.ConfigRepository
import io.stipop.models.body.InitSdkBody
import io.stipop.view.StickerPickerView
import io.stipop.view.PackageDetailBottomSheetFragment
import io.stipop.view.SearchActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class Stipop(
    private val activity: Activity,
    private var stipopButton: StipopImageView? = null,
    val delegate: StipopDelegate
) : StickerPickerView.VisibleStateListener {
    companion object {

        private val mainScope = CoroutineScope(Job() + Dispatchers.Main)

        private val configRepository: ConfigRepository by lazy { ConfigRepository(StipopApi.create()) }

        internal lateinit var applicationContext: Context

        @SuppressLint("StaticFieldLeak")
        internal var instance: Stipop? = null

        var userId = "-1"
            private set

        var lang = "en"
            private set

        var countryCode = "US"
            private set

        internal var currentKeyboardHeight = 0
            private set

        fun configure(context: Context, callback: ((isSuccess: Boolean) -> Unit)? = null) {
            mainScope.launch {
                configRepository.postConfigSdk()
            }
            Config.configure(context, callback = { result ->
                configRepository.isConfigured = result
                callback?.let { callback -> callback(result) }
            })
        }

        fun connect(
            activity: Activity,
            userId: String,
            delegate: StipopDelegate,
            stipopButton: StipopImageView? = null,
            locale: Locale = Locale.getDefault(),
            taskCallBack: ((isSuccess: Boolean) -> Unit)? = null
        ) {
            Stipop.userId = userId
            Stipop.lang = locale.language
            Stipop.countryCode = locale.country

            if (!configRepository.isConfigured) {
                Log.e(
                    "STIPOP-SDK",
                    "Stipop SDK connect failed. Please call Stipop.configure(context) first."
                )
                taskCallBack?.let { it(false) }
            } else {
                Log.d(
                    "STIPOP-SDK",
                    "Stipop SDK connect succeeded. Please call Stipop.showKeyboard() or Stipop.showSearch()"
                )
                applicationContext = activity.applicationContext
                mainScope.launch {
                    configRepository.postInitSdk(
                        initSdkBody = InitSdkBody(
                            userId = Stipop.userId,
                            lang = lang
                        )
                    )
                    Stipop(activity, stipopButton, delegate).apply {
                        connectView()
                        connectIcon()
                    }.run {
                        instance = this
                        taskCallBack?.let { it(true) }
                    }
                }
            }
        }

        @Deprecated(
            "Please use connect(activity, userId, delegate, stipopButton, locale) instead. This method will be modified soon.",
            ReplaceWith(
                "connect(activity, userId, delegate, stipopButton, Locale(lang, countryCode), taskCallBack)",
                "io.stipop.Stipop.Companion.connect",
                "java.util.Locale"
            )
        )
        fun connect(
            activity: Activity,
            stipopButton: StipopImageView? = null,
            userId: String,
            lang: String,
            countryCode: String,
            delegate: StipopDelegate,
            taskCallBack: ((isSuccess: Boolean) -> Unit)? = null
        ) {
            connect(
                activity,
                userId,
                delegate,
                stipopButton,
                Locale(lang, countryCode),
                taskCallBack
            )
        }

        fun showSearch() = instance?.showSearch()

        fun showKeyboard() = instance?.showKeyboard()

        fun hideKeyboard() = instance?.hideKeyboard()

        fun showStickerPackage(fragmentManager: FragmentManager, packageId: Int) =
            instance?.showStickerPackage(fragmentManager, packageId)

        fun send(
            stickerId: Int,
            keyword: String,
            entrancePoint: String,
            completionHandler: (result: Boolean) -> Unit
        ) {
            mainScope.launch {
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

    private val stickerPickerView: StickerPickerView by lazy { StickerPickerView(activity, this) }
    private var maxTop = 0
    private var maxBottom = 0
    private lateinit var rootView: View

    private fun connectView() {
        setSpvHeight()
    }

    private fun connectIcon() {
        stipopButton?.setImageResource(Config.getStickerIconResourceId(activity))
        stipopButton?.setIconDefaultsColor()
    }

    private fun enableStickerIcon() {
        stipopButton?.setTint()
    }

    private fun disableStickerIcon() {
        stipopButton?.clearTint()
    }

    private fun showSearch() {
        Intent(activity, SearchActivity::class.java).run {
            activity.startActivity(this)
        }
    }

    private fun showKeyboard() {
        if (stickerPickerView.isShowing) {
            stickerPickerView.dismiss()
        } else {
            stickerPickerView.wantShowing = true
            if (currentKeyboardHeight == 0) {
                Utils.showKeyboard(instance!!.activity)
            } else {
                stickerPickerView.show()
            }
        }
    }

    private fun hideKeyboard() {
        stickerPickerView.dismiss()
    }

    private fun showStickerPackage(fragmentManager: FragmentManager, packageId: Int) {
        Utils.hideKeyboard(activity)
        PackageDetailBottomSheetFragment.newInstance(packageId, Constants.Point.EXTERNAL)
            .showNow(fragmentManager, Constants.Tag.EXTERNAL)
    }

    private fun setSpvHeight() {
        rootView = activity.window.decorView.findViewById(android.R.id.content) as View
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDifference = getBottomChangedHeight()
            if (heightDifference > Utils.pxToDp(100)) {
                currentKeyboardHeight = heightDifference
                stickerPickerView.let { spv ->
                    if (!spv.isShowing && spv.wantShowing) {
                        spv.height = currentKeyboardHeight
                        spv.show()
                    }
                }
            } else {
                currentKeyboardHeight = 0
                stickerPickerView.dismiss()
            }
        }
    }

    private fun getBottomChangedHeight(): Int {
        val visibleFrameSize = Rect()
        rootView.getWindowVisibleDisplayFrame(visibleFrameSize)
        val screenHeight = Utils.getScreenHeight(activity)
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
        return screenHeight - maxTop - visibleFrameHeight + maxBottom
    }

    override fun onSpvVisibleState(isVisible: Boolean) {
        when (isVisible) {
            true -> {
                enableStickerIcon()
            }
            false -> {
                disableStickerIcon()
            }
        }
    }

}