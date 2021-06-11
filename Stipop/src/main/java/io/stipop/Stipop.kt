package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import io.stipop.activity.SearchActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Stipop {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var activity: Activity

        @SuppressLint("StaticFieldLeak")
        private lateinit var stipopButton:ImageView

        var userId = -1
        var lang = "en"
        var countryCode = "US"

        private var connected = false

        fun configure(context:Context) {

            val jsonString = getJsonDataFromAsset(context) ?: return

            try {
                val json = JSONObject(jsonString)
                Config.parse(json)
            } catch (e: JSONException) {
                e.printStackTrace()

                println("")
                println("")
                println("==========================================")
                println("Stipop configuration check-out failed.")
                println("==========================================")
                println("")
                println("")
            }



        }

        private fun getJsonDataFromAsset(context: Context): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open("Stipop.json").bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        fun connect(activity: Activity, stipopButton:ImageView, userId:Int, lang: String, countryCode:String) {
            this.activity = activity
            this.stipopButton = stipopButton
            this.userId = userId
            this.lang = lang
            this.countryCode = countryCode

            this.stipopButton.setImageResource(R.mipmap.ic_sticker_normal)

            this.connected = true
        }

        fun show() {
            if (!this.connected) {
                return
            }

            this.enableStickerIcon()

            val intent = Intent(this.activity, SearchActivity::class.java)
            this.activity.startActivity(intent)
        }

        private fun enableStickerIcon() {
            if (this.connected) {
                this.stipopButton.setImageResource(R.mipmap.ic_sticker_active)
            }
        }
    }
}