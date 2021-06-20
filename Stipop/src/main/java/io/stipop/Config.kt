package io.stipop

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Config {
    companion object {

        val baseUrl = "https://messenger.stipop.io/v1"

        lateinit var apikey:String
        var stickerIconNormalLight = "ic_sticker_normal"
        var stickerIconActiveLight = "ic_sticker_active"

        lateinit var allStickerType:String
        lateinit var allowPremium:String

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

        fun parse(json: JSONObject) {
            Config.apikey = Utils.getString(json, "api_key")
            Config.allStickerType = "A"

            val storePolicy = json.getJSONObject("StorePolicy")
            allowPremium = Utils.getString(storePolicy, "allowPremium")


            val stickerIcon = json.getJSONObject("StickerIcon")

            val stickerIconNormal = stickerIcon.getJSONObject("normal")

            this.stickerIconNormalLight = Utils.getString(stickerIconNormal, "light")
            val stickerIconNormalDark = Utils.getString(stickerIconNormal, "dark")

            val stickerIconActive = stickerIcon.getJSONObject("active")

            this.stickerIconActiveLight = Utils.getString(stickerIconActive, "light")
            val stickerIconActiveDark = Utils.getString(stickerIconActive, "dark")

        }

    }
}