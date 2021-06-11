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

        fun parse(json: JSONObject) {
            Config.apikey = Utils.getString(json, "api_key")

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