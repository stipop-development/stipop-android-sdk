package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.View
import io.stipop.view_common.StickerPackageActivity
import io.stipop.view_keyboard.KeyboardPopup
import io.stipop.view_search.SearchActivity
import io.stipop.api.APIClient
import io.stipop.api.StipopApi
import io.stipop.custom.StipopImageView
import io.stipop.data.BaseRepository
import io.stipop.data.ConfigRepository
import io.stipop.models.SPPackage
import io.stipop.models.SPSticker
import io.stipop.models.body.InitSdkBody
import io.stipop.models.body.UserIdBody
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.subscribe
import org.json.JSONObject
import java.io.IOException

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

        val configRepository: ConfigRepository by lazy { ConfigRepository(StipopApi.create()) }

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
            scope.launch {
                StipopApi.create().trackConfig(UserIdBody())
            }
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

            val requestBody = InitSdkBody(userId = Stipop.userId, language = Stipop.countryCode)
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

        val scope = CoroutineScope(Job() + Dispatchers.Main)

        fun showSearch() {
            if (instance == null) {
                return
            }

            instance!!.showSearch()
        }

        fun showKeyboard() {
            if (instance == null) {
                return
            }

            instance!!.showKeyboard()
        }

        /*
        fun show() {
            if (instance == null) {
                return
            }

            instance!!.show()
        }

        fun detail(packageId: Int) {
            if (instance == null) {
                return
            }

            instance!!.detail(packageId)
        }
        */

        internal fun send(
            stickerId: Int,
            keyword: String,
            entrancePoint: String,
            completionHandler: (result: Boolean) -> Unit
        ) {
            if (instance == null || instance?.connected == false) {
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


    private var connected = false
    private var stickerIconEnabled = false

    fun connect() {
        this.stipopButton.setImageResource(Config.getStickerIconResourceId(this.activity))

        this.connected = true

        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View

        this.setSizeForSoftKeyboard()
    }

    fun show() {
        if (!this.connected) {
            return
        }

        if (this.stickerIconEnabled) {
            this.showKeyboard()
        } else {
            this.enableStickerIcon()

            val intent = Intent(this.activity, SearchActivity::class.java)
            this.activity.startActivity(intent)
        }
    }

    fun goPackageDetail(packageId: Int, entrancePoint: String) {
        if (!this.connected) {
            return
        }
        Intent(this.activity, StickerPackageActivity::class.java).apply {
            putExtra("packageId", packageId)
            putExtra(Constants.IntentKey.ENTRANCE_POINT, entrancePoint)
        }.run {
            activity.startActivity(this)
        }
    }

    fun send(stickerId: Int, searchKeyword: String, completionHandler: (result: Boolean) -> Unit) {


        val params = JSONObject()
        params.put("userId", userId)
        params.put("p", searchKeyword)
        params.put("lang", lang)
        params.put("countryCode", countryCode)

        APIClient.post(
            activity,
            APIClient.APIPath.ANALYTICS_SEND.rawValue + "/${stickerId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {
                var success = true

                if (response.isNull("header")) {
                    success = false
                } else {

                    val header = response.getJSONObject("header")

                    if (Utils.getString(header, "status") != "success" || Utils.getInt(
                            header,
                            "code",
                            -1
                        ) == -1
                    ) {
                        success = false
                    }

                }

                completionHandler(success)
            } else {
                completionHandler(false)
            }
        }
    }

    private fun enableStickerIcon() {
        if (this.connected) {
            this.stipopButton.setTint()

            this.stickerIconEnabled = true
        }
    }

    private fun disableStickerIcon() {
        if (this.connected) {
            this.stipopButton.clearTint()
        }
    }

    private fun showSearch() {
        if (!this.connected) {
            return
        }

        // this.enableStickerIcon()

        val intent = Intent(this.activity, SearchActivity::class.java)
        this.activity.startActivity(intent)
    }

    private fun showKeyboard() {
        if (!this.connected) {
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