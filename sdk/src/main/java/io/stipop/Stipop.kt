package io.stipop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.stipop.api.StipopApi
import io.stipop.custom.StipopImageView
import io.stipop.data.ConfigRepository
import io.stipop.models.body.InitSdkBody
import io.stipop.view.PackDetailFragment
import io.stipop.view.StickerSearchView
import io.stipop.view.StickerPickerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class Stipop(
    private val activity: FragmentActivity,
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

        internal var fromTopToVisibleFramePx = 0
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

        var canRetryIfConnectFailed = true

        fun connect(
            activity: FragmentActivity,
            userId: String,
            delegate: StipopDelegate,
            stipopButton: StipopImageView? = null,
            locale: Locale = Locale.getDefault(),
            taskCallBack: ((isSuccess: Boolean) -> Unit)? = null
        ) {
            Stipop.userId = userId
            StipopUtils.controlLocale(locale).let {
                lang = it.language
                countryCode = it.country
            }
            if (!configRepository.isConfigured) {
                if(canRetryIfConnectFailed){
                    Log.w("STIPOP-SDK", "Stipop SDK not connected. Because 'canRetryIfConnectFailed' is True, SDK calls 'configure(context)' automatically just once.")
                    configure(activity, callback = {
                        if(it) connect(activity, userId, delegate, stipopButton, locale, taskCallBack)
                        canRetryIfConnectFailed = false
                    })
                }else{
                    Log.e("STIPOP-SDK", "Stipop SDK connect failed. Please call Stipop.configure(context) first.")
                    taskCallBack?.let {
                        it(false)
                    }
                }
            } else {
                Log.v("STIPOP-SDK", "Stipop SDK connect succeeded. You can use SDK by calling Stipop.showKeyboard() or Stipop.showSearch() and implementing StipopDelegate interface.")
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
            activity: FragmentActivity,
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

        /**
         * Use When Sticker Picker View Height Modifying is needed.
         */
        fun setKeyboardAdditionalHeightOffset(height: Int) {
            instance?.let {
                instance!!.spvAdditionalHeightOffset = height
            } ?: kotlin.run {
                throw InstantiationException("Stipop Instance Not Connected. Please call this method in callback of Stipop.connect(), or when Stipop.connect() is completed.")
            }
        }

        fun showSearch() = instance?.showSearch()

        fun showKeyboard() = instance?.showKeyboard()

        fun hideKeyboard() = instance?.hideKeyboard()

        fun showStickerPackage(fragmentManager: FragmentManager, packageId: Int) =
            instance?.showStickerPackage(fragmentManager, packageId)

        internal fun send(
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

    private var spvAdditionalHeightOffset = 0
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
        StickerSearchView.newInstance().showNow(activity.supportFragmentManager, Constants.Tag.SSV)
    }

    private fun showKeyboard() {
        if (stickerPickerView.isShowing) {
            stickerPickerView.dismiss()
        } else {
            stickerPickerView.wantShowing = true
            if (currentKeyboardHeight == 0) {
                StipopUtils.showKeyboard(instance!!.activity)
            }
            stickerPickerView.show(fromTopToVisibleFramePx)
        }
    }

    private fun hideKeyboard() {
        stickerPickerView.dismiss()
    }

    private fun showStickerPackage(fragmentManager: FragmentManager, packageId: Int) {
        StipopUtils.hideKeyboard(activity)
        PackDetailFragment.newInstance(packageId, Constants.Point.EXTERNAL)
            .showNow(fragmentManager, Constants.Tag.EXTERNAL)
    }

    private fun setSpvHeight() {
        rootView = activity.window.decorView.findViewById(android.R.id.content) as View
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val fullSizeHeight = StipopUtils.getScreenHeight(activity)
            val visibleFrameRect = Rect()
            rootView.getWindowVisibleDisplayFrame(visibleFrameRect)
            fromTopToVisibleFramePx = visibleFrameRect.bottom

            val heightDifference =
                fullSizeHeight - fromTopToVisibleFramePx + spvAdditionalHeightOffset
            if (heightDifference > StipopUtils.pxToDp(100)) {
                currentKeyboardHeight = heightDifference
                stickerPickerView.let { spv ->
                    spv.height = currentKeyboardHeight
                    if (spv.wantShowing && !spv.isShowing) {
                        spv.show(fromTopToVisibleFramePx)
                    }
                }
            } else {
                currentKeyboardHeight = 0
                stickerPickerView.dismiss()
            }
        }
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