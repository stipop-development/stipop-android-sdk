package io.stipop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Config {
    companion object {

        val baseUrl = "https://messenger.stipop.io/v1"

        lateinit var apikey: String
        var stickerIconNormalLight = "ic_sticker_normal"
        var stickerIconActiveLight = "ic_sticker_active"

        lateinit var allowPremium: String

        var useLightMode = true

        var themeColor = ""
        var themeGroupedBgColor = ""
        var themeContentsBgColor = ""

        var searchbarBgColor = ""
        var searchbarIconName = ""
        var searchbarDeleteIconName = ""

        var storeListType = ""

        var storeTrendingUseBackgroundColor = false
        var storeTrendingBackgroundColor = ""
        var storeTrendingOpacity = 0.0

        var storeDownloadIconName = ""
        var storeCompleteIconName = ""

        var keyboardStoreIconName = ""
        var keyboardNumOfColumns = 3

        fun configure(context: Context) {

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
                jsonString =
                    context.assets.open("Stipop.json").bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        fun parse(json: JSONObject) {
            Config.apikey = Utils.getString(json, "api_key")

            val theme = json.getJSONObject("Theme")
            useLightMode = Utils.getBoolen(theme, "useLightMode", true)

            val color = theme.getJSONObject("color")
            val groupedBgColor = theme.getJSONObject("groupedBgColor")
            val contentsBgColor = theme.getJSONObject("contentsBgColor")


            val storePolicy = json.getJSONObject("StorePolicy")
            allowPremium = Utils.getString(storePolicy, "allowPremium")


            val stickerIcon = json.getJSONObject("StickerIcon")

            val stickerIconNormal = stickerIcon.getJSONObject("normal")

            this.stickerIconNormalLight = Utils.getString(stickerIconNormal, "light")
            val stickerIconNormalDark = Utils.getString(stickerIconNormal, "dark")

            val stickerIconActive = stickerIcon.getJSONObject("active")

            this.stickerIconActiveLight = Utils.getString(stickerIconActive, "light")
            val stickerIconActiveDark = Utils.getString(stickerIconActive, "dark")

            val search = json.getJSONObject("Search")

            val searchbarIcon = search.getJSONObject("searchbarIcon")
            val searchbarDeleteIcon = search.getJSONObject("searchbarDeleteIcon")
            val searchbarColor = search.getJSONObject("searchbarColor")

            val liteStore = json.getJSONObject("LiteStore")

            storeListType = Utils.getString(liteStore, "listType")

            val trending = liteStore.getJSONObject("trending")

            storeTrendingUseBackgroundColor = Utils.getBoolen(trending, "useBackgroundColor", false)
            storeTrendingBackgroundColor = Utils.getString(trending, "backgroundColor")
            storeTrendingOpacity = Utils.getDouble(trending, "opacity")

            val downloadIcon = liteStore.getJSONObject("downloadIcon")
            val completeIcon = liteStore.getJSONObject("completeIcon")


            val keyboard = json.getJSONObject("Keyboard")

            keyboardNumOfColumns = Utils.getInt(keyboard, "numOfColumns", 3)

            val liteStoreIcon = keyboard.getJSONObject("liteStoreIcon")

            if (useLightMode) {
                themeColor = Utils.getString(color, "light", "#ffffff")
                themeGroupedBgColor = Utils.getString(groupedBgColor, "light", "#f7f8f9")
                themeContentsBgColor = Utils.getString(contentsBgColor, "light", "#ffffff")

                searchbarBgColor = Utils.getString(searchbarColor, "light")
                searchbarIconName = Utils.getString(searchbarIcon, "light")
                searchbarDeleteIconName = Utils.getString(searchbarDeleteIcon, "light")

                storeDownloadIconName = Utils.getString(downloadIcon, "light")
                storeCompleteIconName = Utils.getString(completeIcon, "light")

                keyboardStoreIconName = Utils.getString(liteStoreIcon, "light")
            } else {
                themeColor = Utils.getString(color, "dark", "#2e363a")
                themeGroupedBgColor = Utils.getString(groupedBgColor, "dark", "#1e2427")
                themeContentsBgColor = Utils.getString(contentsBgColor, "dark", "#2e363a")

                searchbarBgColor = Utils.getString(searchbarColor, "dark")
                searchbarIconName = Utils.getString(searchbarIcon, "dark")
                searchbarDeleteIconName = Utils.getString(searchbarDeleteIcon, "dark")

                storeDownloadIconName = Utils.getString(downloadIcon, "dark")
                storeCompleteIconName = Utils.getString(completeIcon, "dark")

                keyboardStoreIconName = Utils.getString(liteStoreIcon, "dark")
            }
        }

        fun getKeyboardStoreResourceId(context: Context): Int {
            var imageId: Int
            if (keyboardStoreIconName.isNotEmpty()) {
                imageId = Utils.getResource(keyboardStoreIconName, context)
            } else {
                if (useLightMode) {
                    imageId = R.mipmap.ic_store
                } else {
                    imageId = R.mipmap.ic_store_dark
                }
            }
            return imageId
        }

        fun getSearchbarResourceId(context: Context): Int {
            var imageId: Int
            if (searchbarIconName.isNotEmpty()) {
                imageId = Utils.getResource(searchbarIconName, context)
            } else {
                if (useLightMode) {
                    imageId = R.mipmap.icon_search
                } else {
                    imageId = R.mipmap.icon_search_dark
                }
            }
            return imageId
        }

        fun getEraseResourceId(context: Context): Int {
            var imageId: Int
            if (searchbarDeleteIconName.isNotEmpty()) {
                imageId = Utils.getResource(searchbarDeleteIconName, context)
            } else {
                if (useLightMode) {
                    imageId = R.mipmap.icon_erase
                } else {
                    imageId = R.mipmap.icon_erase_dark
                }
            }
            return imageId
        }

        fun getDownloadIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_download
            if (storeDownloadIconName.isNotEmpty()) {
                imageId = Utils.getResource(storeDownloadIconName, context)
            }
            return imageId
        }

        fun getCompleteIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_downloaded
            if (storeCompleteIconName.isNotEmpty()) {
                imageId = Utils.getResource(storeCompleteIconName, context)
            }
            return imageId
        }

        fun getErrorImage(): Int {
            var imageId = R.mipmap.error
            if (!useLightMode) {
                imageId = R.mipmap.error_dark
            }
            return imageId
        }

        fun getUnderLineColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_2e363a)
            }
            return color
        }

        fun getStoreNavigationTextColor(context: Context, selected: Boolean): Int {
            var color = ContextCompat.getColor(context, R.color.c_646f7c)
            if (selected) {
                if (!useLightMode) {
                    color = ContextCompat.getColor(context, R.color.c_f3f4f5)
                } else {
                    color = ContextCompat.getColor(context, R.color.c_374553)
                }
            } else {
                if (!useLightMode) {
                    color = ContextCompat.getColor(context, R.color.c_646f7c)
                } else {
                    color = ContextCompat.getColor(context, R.color.c_c6c8cf)
                }
            }
            return color
        }

        fun getTitleTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_646f7c)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_c6c8cf)
            }
            return color
        }

        fun getAllStickerPackageNameTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_374553)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            }
            return color
        }

        fun getAllStickerArtistNameTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_646f7c)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_c6c8cf)
            }
            return color
        }

        fun setStoreTrendingBackground(context: Context, drawable: GradientDrawable): Int {
            var color = ContextCompat.getColor(context, R.color.c_eeeeee)

            if (storeTrendingUseBackgroundColor) {
                color = Color.parseColor(storeTrendingBackgroundColor)
            }

            drawable.setColor(color)

            return color
        }

    }
}