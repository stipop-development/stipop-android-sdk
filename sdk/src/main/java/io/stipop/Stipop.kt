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
import io.stipop.view.pickerview.StickerPickerCustomFragment
import io.stipop.view.pickerview.StickerPickerKeyboardView
import io.stipop.view.pickerview.listener.VisibleStateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class Stipop(
    private val activity: FragmentActivity,
    private var stipopButton: StipopImageView? = null,
    val delegate: StipopDelegate
) : VisibleStateListener {
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
            stickerPickerCustomFragment: StickerPickerCustomFragment? = null,
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
                        if(it) connect(activity, userId, delegate, stipopButton, stickerPickerCustomFragment, locale, taskCallBack)
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
                        when(Config.pickerViewLayoutOnKeyboard){
                            true -> {
                                stickerPickerKeyboardView = StickerPickerKeyboardView(activity)
                                stickerPickerKeyboardView?.setDelegate(this)
                                getStickerPickerKeyboardViewHeight()
                            }
                            false -> {
                                this.stickerPickerCustomFragment = stickerPickerCustomFragment
                                this.stickerPickerCustomFragment?.setDelegate(this)
                            }
                        }
                        connectIcon()
                    }.run {
                        instance = this
                        taskCallBack?.let { it(true) }
                    }
                }
            }
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

        fun show() = instance?.showPickerView()

        fun hide() = instance?.hidePickerView()

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

        fun getCurrentKeyboardHeight(): Int{
            return currentKeyboardHeight
        }
    }

    private var stickerPickerKeyboardView: StickerPickerKeyboardView? = null
    private var stickerPickerCustomFragment: StickerPickerCustomFragment? = null

    private var spvAdditionalHeightOffset = 0
    private lateinit var rootView: View

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

    private fun showPickerView() {
      when(Config.pickerViewLayoutOnKeyboard){
          true -> showPickerKeyboardView()
          false -> showPickerCustomView()
      }
    }
    private fun showPickerKeyboardView(){
        when(stickerPickerKeyboardView?.isShowing){
            true -> {
                hidePickerKeyboardView()
            }
            false -> {
                stickerPickerKeyboardView?.wantShowing = true
                if (currentKeyboardHeight == 0) {
                    StipopUtils.showKeyboard(instance!!.activity)
                }
                stickerPickerKeyboardView?.show(fromTopToVisibleFramePx)
            }
        }
    }

    private fun showPickerCustomView(){
        when(stickerPickerCustomFragment?.isShowing()){
            true -> {
                stickerPickerCustomFragment?.dismiss()
            }
            false -> {
                stickerPickerCustomFragment?.show()
            }
        }
    }

    private fun hidePickerView() {
        when(Config.pickerViewLayoutOnKeyboard){
            true -> hidePickerKeyboardView()
            false -> hidePickerCustomView()
        }
    }

    private fun hidePickerKeyboardView(){
        StipopUtils.hideKeyboard(activity)
        stickerPickerKeyboardView?.dismiss()
    }

    private fun hidePickerCustomView(){
        stickerPickerCustomFragment?.dismiss()
    }

    private fun showStickerPackage(fragmentManager: FragmentManager, packageId: Int) {
        StipopUtils.hideKeyboard(activity)
        PackDetailFragment.newInstance(packageId, Constants.Point.EXTERNAL)
            .showNow(fragmentManager, Constants.Tag.EXTERNAL)
    }

    private fun getStickerPickerKeyboardViewHeight() {
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
                stickerPickerKeyboardView.let { spv ->
                    spv?.let {
                        it.height = currentKeyboardHeight
                        if (it.wantShowing && !it.isShowing) {
                            it.show(fromTopToVisibleFramePx)
                        }
                    }
                }
            } else {
                currentKeyboardHeight = 0
                hidePickerKeyboardView()
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