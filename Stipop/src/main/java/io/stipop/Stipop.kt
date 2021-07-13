package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import io.stipop.activity.DetailActivity
import io.stipop.activity.Keyboard
import io.stipop.activity.SearchActivity
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import org.json.JSONObject
import java.io.IOException

interface StipopDelegate {
    fun onStickerSelected(sticker: SPSticker)
    fun canDownload(spPackage:SPPackage): Boolean
}

class Stipop(private val activity: Activity, private val stipopButton: StipopImageView, val delegate: StipopDelegate) {

    companion object {

        @SuppressLint("StaticFieldLeak")
        var instance:Stipop? = null

        var userId = "-1"
            private set

        var lang = "en"
            private set

        var countryCode = "us"
            private set

        var keyboardHeight = 0
            private set

        fun configure(context:Context) {
            Config.configure(context)
        }

        fun connect(activity: Activity, stipopButton: StipopImageView, userId: String, lang: String, countryCode: String, delegate: StipopDelegate) {

            Stipop.userId = userId
            Stipop.lang = lang
            Stipop.countryCode = countryCode

            instance = Stipop(activity, stipopButton, delegate)

            instance!!.connect()
        }

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

        internal fun send(stickerId: Int, keyword: String, completionHandler: (result: Boolean) -> Unit) {
            if (instance == null) {
                return
            }

            instance!!.send(stickerId, keyword, completionHandler)
        }
    }

    private var maxTop = 0
    private var maxBottom = 0
    private var keyboard: Keyboard? = null
    private lateinit var rootView: View


    private var connected = false
    private var stickerIconEnabled = false

    fun connect() {
        this.stipopButton.setImageResource(R.mipmap.ic_sticker_border_3)

        this.connected = true

        this.enableStickerIcon()

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

    fun detail(packageId: Int) {
        if (!this.connected) {
            return
        }

        val intent = Intent(this.activity, DetailActivity::class.java)
        intent.putExtra("packageId", packageId)
        this.activity.startActivity(intent)
    }

    fun send(stickerId: Int, searchKeyword: String, completionHandler: (result: Boolean) -> Unit) {
        if (!this.connected) {
            return
        }

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

                    if (Utils.getString(header, "status") != "success" || Utils.getInt(header, "code", -1) == -1) {
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
            // this.stipopButton.setImageResource(R.mipmap.ic_sticker_active)
            this.stipopButton.setTint()

            this.stickerIconEnabled = true
        }
    }

    private fun showSearch() {
        if (!this.connected) {
            return
        }

        val intent = Intent(this.activity, SearchActivity::class.java)
        this.activity.startActivity(intent)
    }

    private fun showKeyboard() {
        if (!this.connected) {
            return
        }

        if(keyboard == null) {
            keyboard = Keyboard(this.activity)
        }

        if (keyboard!!.popupWindow.isShowing) {
            this.keyboard!!.canShow = false
            keyboard!!.hide()
        } else {
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

                if(this.keyboard != null) {
                    this.keyboard!!.popupWindow.height = keyboardHeight

                    if (!this.keyboard!!.popupWindow.isShowing) {
                        this.keyboard!!.show()
                    }
                }
            } else {
                if(this.keyboard != null) {
                    this.keyboard!!.popupWindow.height = 0
                    this.keyboard!!.hide()
                }
            }
        }
    }

}