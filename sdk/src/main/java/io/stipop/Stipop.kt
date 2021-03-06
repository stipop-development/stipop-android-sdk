package io.stipop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.stipop.api.StipopApi
import io.stipop.custom.StipopImageView
import io.stipop.data.ConfigRepository
import io.stipop.event.SAuthDelegate
import io.stipop.models.SPSticker
import io.stipop.models.body.InitSdkBody
import io.stipop.models.body.TrackErrorBody
import io.stipop.models.enum.StipopApiEnum
import io.stipop.s_auth.SAuthManager
import io.stipop.s_auth.TrackUsingStickerEnum
import io.stipop.view.PackDetailFragment
import io.stipop.view.StickerSearchView
import io.stipop.view.pickerview.StickerPickerCustomFragment
import io.stipop.view.pickerview.StickerPickerKeyboardView
import io.stipop.view.pickerview.StickerPickerViewClass
import io.stipop.view.pickerview.listener.VisibleStateListener
import io.stipop.view.viewmodel.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*


class Stipop(
    private val activity: FragmentActivity,
    private var stipopButton: StipopImageView? = null,
    val delegate: StipopDelegate
) : VisibleStateListener {

    companion object {

        internal var sAuthDelegate: SAuthDelegate? = null
        internal var keyboardHeightDelegate: StipopKeyboardHeightDelegate? = null

        private val mainScope = CoroutineScope(Job() + Dispatchers.Main)

        internal val configRepository: ConfigRepository by lazy { ConfigRepository(StipopApi.create()) }

        internal var stickerPickerViewClass: StickerPickerViewClass? = null
        internal var stickerPickerViewModel: StickerPickerViewModel? = null
        internal var storeHomeViewModel: StoreHomeViewModel? = null
        internal var storeMyStickerViewModel: StoreMyStickerViewModel? = null
        internal var storeNewsViewModel: StoreNewsViewModel? = null
        internal var packDetailViewModel: PackDetailViewModel? = null

        internal lateinit var applicationContext: Context

        @SuppressLint("StaticFieldLeak")
        internal var instance: Stipop? = null

        var userId = "-1"
            private set

        var lang = "en"
            private set

        var countryCode = "US"
            private set

        internal var currentPickerViewHeight = 0
            private set

        internal var fromTopToVisibleFramePx = 0
            private set

        private var inputMode: WindowSoftInputModeAdjustEnum? = null

        private var canRetryIfConnectFailed = true

        fun setAccessToken(accessToken: String){
            SAuthManager.setAccessToken(accessToken)
        }

        fun configure(context: Context, sAuthDelegate: SAuthDelegate? = null, callback: ((isSuccess: Boolean) -> Unit)? = null) {
            sAuthDelegate?.let {
                this.sAuthDelegate = it
            }
            Config.configure(context, callback = { result ->
                mainScope.launch {
                    configRepository.isConfigured = result
                    configRepository.postConfigSdk()
                    callback?.let { callback -> callback(result) }
                }
            })
        }

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
                connectSuccessInit(activity, stipopButton, delegate, taskCallBack)
            }
        }

        private fun connectSuccessInit(activity: FragmentActivity,
                                       stipopButton: StipopImageView?,
                                       stipopDelegate: StipopDelegate,
                                       taskCallBack: ((isSuccess: Boolean) -> Unit)?){
            applicationContext = activity.applicationContext
            mainScope.launch {
                postInitSDK()
                try {
                    Stipop(activity, stipopButton, stipopDelegate).apply {
                        when (Config.pickerViewLayoutOnKeyboard) {
                            true -> {
                                stickerPickerKeyboardView = StickerPickerKeyboardView(activity)
                                stickerPickerKeyboardView?.setDelegate(this)
                                setInputMode()
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
                } catch(exception: Exception){
                    trackError(exception)
                }
            }
        }

        suspend fun postInitSDK(){
            try {
                configRepository.postInitSdk(
                    initSdkBody = InitSdkBody(
                        userId = userId,
                        lang = lang
                    )
                )
            } catch(exception: HttpException){
                when(exception.code()){
                    401 -> sAuthDelegate?.httpException(StipopApiEnum.INIT_SDK, exception)
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

        fun showStickerPackage(fragmentManager: FragmentManager, packageId: Int) {
            instance?.showStickerPackage(fragmentManager, packageId)
        }

        internal fun send(
            trackUsingStickerEnum: TrackUsingStickerEnum,
            sticker: SPSticker,
            entrancePoint: String,
            completionHandler: (result: Boolean) -> Unit
        ) {
            mainScope.launch {
                try {
                    configRepository.postTrackUsingSticker(
                        stickerId = sticker.stickerId.toString(),
                        userId = userId,
                        query = sticker.keyword,
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
                } catch(exception: HttpException){
                    when(exception.code()){
                        401 -> {
                            completionHandler(false)
                            SAuthManager.setPostTrackUsingStickerData(trackUsingStickerEnum, sticker)
                            sAuthDelegate?.httpException(StipopApiEnum.TRACK_USING_STICKER, exception)
                        }
                    }
                }
            }
        }

        fun setKeyboardHeightDelegate(keyboardHeightDelegate: StipopKeyboardHeightDelegate){
            Stipop.keyboardHeightDelegate = keyboardHeightDelegate
        }

        internal fun trackError(exception: Exception){
            val stringWriter = StringWriter()
            exception.printStackTrace(PrintWriter(stringWriter))
            val exceptionAsString: String = stringWriter.toString()

            GlobalScope.launch {
                val response = StipopApi.create().trackError(Stipop.userId, TrackErrorBody(exceptionAsString))
                if(response.code() == 401){
                    SAuthManager.setTrackErrorData(exception)
                    Stipop.sAuthDelegate?.httpException(StipopApiEnum.TRACK_ERROR, HttpException((response)))
                }
            }
        }
    }

    private var stickerPickerKeyboardView: StickerPickerKeyboardView? = null
    private var stickerPickerCustomFragment: StickerPickerCustomFragment? = null

    private var spvAdditionalHeightOffset = 0
    private lateinit var rootView: View

    private val fullSizeHeight = StipopUtils.getScreenHeight(activity)

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

    private fun setInputMode(){
        if(inputMode == null) {
            val inputValue = activity.window.attributes.softInputMode
            inputMode = WindowSoftInputModeUtils().isInputSoftModeNothing(inputValue)
        }
    }

    private fun getStickerPickerKeyboardViewHeight(){
        try {
            if(inputMode == WindowSoftInputModeAdjustEnum.ADJUST_NOTHING){
                getStickerPickerKeyboardViewHeightAdjustNothing()
            } else {
                getStickerPickerKeyboardViewHeightAdjust()
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    private fun getStickerPickerKeyboardViewHeightAdjustNothing() {
        StipopHeightProvider(activity, StipopHeightProviderTypeEnum.FROM_TOP_TO_VISIBLE_FRAME_PX).init().setHeightListener(object: StipopHeightProvider.StipopHeightListener{
            override fun onHeightChanged(height: Int) {
                try {
                    fromTopToVisibleFramePx = height
                    getStickerPickerKeyboardViewHeightShow()
                } catch(exception: Exception){
                    Stipop.trackError(exception)
                }
            }
        })
    }

    private fun getStickerPickerKeyboardViewHeightAdjust() {
        rootView = activity.window.decorView.findViewById(android.R.id.content) as View
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                val visibleFrameRect = Rect()
                rootView.getWindowVisibleDisplayFrame(visibleFrameRect)
                fromTopToVisibleFramePx = visibleFrameRect.bottom
                getStickerPickerKeyboardViewHeightShow()
            } catch(exception: Exception){
                Stipop.trackError(exception)
            }
        }
    }

    private fun getStickerPickerKeyboardViewHeightShow(isAdjustNothing: Boolean = false){
        val insets: WindowInsetsCompat? = ViewCompat.getRootWindowInsets(activity.window.decorView)
        val bottomInset = insets?.systemWindowInsetBottom ?: 0

        val heightDifference = fullSizeHeight - fromTopToVisibleFramePx + spvAdditionalHeightOffset
        if (heightDifference > StipopUtils.pxToDp(100)) {
            if(Build.VERSION.SDK_INT < 30){
                when(isAdjustNothing){
                    true -> currentPickerViewHeight = -bottomInset
                    false -> currentPickerViewHeight = heightDifference - StipopUtils.getBottomNavigationBarHeight()
                }
            } else {
                currentPickerViewHeight = heightDifference
            }
            stickerPickerKeyboardView.let { spv ->
                spv?.let {
                    it.height = currentPickerViewHeight
                    if (it.wantShowing && !it.isShowing) {
                        it.show(fromTopToVisibleFramePx)
                    }
                }
            }
        } else {
            currentPickerViewHeight = 0
            hidePickerKeyboardView()
        }
        keyboardHeightDelegate?.onHeightChanged(currentPickerViewHeight)
    }

    private fun showSearch() {
        mainScope.launch {
            StickerSearchView.newInstance().show(activity.supportFragmentManager, Constants.Tag.SSV)
        }
    }

    private fun showPickerView() {
        mainScope.launch {
            try {
                when (Config.pickerViewLayoutOnKeyboard) {
                    true -> showPickerKeyboardView()
                    false -> showPickerCustomView()
                }
            } catch(exception: Exception){
                Stipop.trackError(exception)
            }
        }
    }

    private fun showPickerKeyboardView(){
        when(stickerPickerKeyboardView?.isShowing){
            true -> {
                hidePickerKeyboardView()
            }
            false -> {
                stickerPickerKeyboardView?.wantShowing = true
                if (currentPickerViewHeight == 0) {
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
        try {
            when(Config.pickerViewLayoutOnKeyboard){
                true -> hidePickerKeyboardView()
                false -> hidePickerCustomView()
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    private fun hidePickerKeyboardView(){
//        StipopUtils.hideKeyboard(activity)
        stickerPickerKeyboardView?.dismiss()
    }

    private fun hidePickerCustomView(){
        stickerPickerCustomFragment?.dismiss()
    }

    private fun showStickerPackage(fragmentManager: FragmentManager, packageId: Int) {
        StipopUtils.hideKeyboard(activity)
        PackDetailFragment.newInstance(packageId, Constants.Point.EXTERNAL).show(fragmentManager, Constants.Tag.EXTERNAL)
    }

    override fun onSpvVisibleState(isVisible: Boolean) {
        try {
            when (isVisible) {
                true -> {
                    enableStickerIcon()
                }
                false -> {
                    disableStickerIcon()
                }
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }
}