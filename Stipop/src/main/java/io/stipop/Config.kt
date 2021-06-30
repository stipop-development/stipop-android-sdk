package io.stipop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.math.roundToInt

class Config {
    companion object {

        const val baseUrl = "https://messenger.stipop.io/v1"

        lateinit var apikey: String
        private var stickerIconNormalLight = "ic_sticker_normal"
        private var stickerIconActiveLight = "ic_sticker_active"

        var useLightMode = true

        var themeColor = "#ffffff"
        var themeGroupedBgColor = "#f7f8f9"
        var themeContentsBgColor = "#ffffff"

        var searchbarRadius = 10
        private var searchNumOfColumns = 3

        var searchTagsHidden = false
        var searchTagBgColor = "#ff855b"

        var searchbarBgColor = ""
        private var searchbarIconName = ""
        private var searchbarDeleteIconName = ""

        var storeListType = ""

        private var storeTrendingUseBackgroundColor = false
        private var storeTrendingBackgroundColor = ""
        private var storeTrendingOpacity = 0.0

        private var storeDownloadIconName = ""
        private var storeCompleteIconName = ""

        private var orderIconName = ""
        private var hideIconName = ""

        private var keyboardStoreIconName = ""
        var keyboardNumOfColumns = 3

        lateinit var allowPremium: String
        var pngPrice: Double = 0.0
        var gifPrice: Double = 0.0

        private var detailBackIconName = ""
        private var detailCloseIconName = ""
        private var detailDownloadButtonColor = ""
        var detailNumOfColumns = 3

        private const val LIGHT_KEY = "light"
        private const val DARK_KEY = "dark"

        fun configure(context: Context) {

            val jsonString = getJsonDataFromAsset(context) ?: return

            try {
                val json = JSONObject(jsonString)
                parse(json)
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

        private fun parse(json: JSONObject) {
            apikey = Utils.getString(json, "api_key")

            val theme = json.getJSONObject("Theme")
            useLightMode = Utils.getBoolen(theme, "useLightMode", true)

            val color = theme.getJSONObject("color")
            val groupedBgColor = theme.getJSONObject("groupedBgColor")
            val contentsBgColor = theme.getJSONObject("contentsBgColor")


            val stickerIcon = json.getJSONObject("StickerIcon")

            val stickerIconNormal = stickerIcon.getJSONObject("normal")

            this.stickerIconNormalLight = Utils.getString(stickerIconNormal, LIGHT_KEY)
            val stickerIconNormalDark = Utils.getString(stickerIconNormal, DARK_KEY)

            val stickerIconActive = stickerIcon.getJSONObject("active")

            this.stickerIconActiveLight = Utils.getString(stickerIconActive, LIGHT_KEY)
            val stickerIconActiveDark = Utils.getString(stickerIconActive, DARK_KEY)

            val search = json.getJSONObject("Search")

            searchbarRadius = Utils.getInt(search, "searchbarRadius", 10)
            searchNumOfColumns = Utils.getInt(search, "numOfColumns", 10)

            val searchbarIcon = search.getJSONObject("searchbarIcon")
            val searchbarDeleteIcon = search.getJSONObject("searchbarDeleteIcon")
            val searchbarColor = search.getJSONObject("searchbarColor")
            val searchTags = search.getJSONObject("searchTags")

            searchTagsHidden = Utils.getBoolen(searchTags, "hidden", false)

            val liteStore = json.getJSONObject("LiteStore")

            storeListType = Utils.getString(liteStore, "listType")

            val trending = liteStore.getJSONObject("trending")

            storeTrendingUseBackgroundColor = Utils.getBoolen(trending, "useBackgroundColor", false)
            storeTrendingBackgroundColor = Utils.getString(trending, "backgroundColor")
            storeTrendingOpacity = Utils.getDouble(trending, "opacity")

            val downloadIcon = liteStore.getJSONObject("downloadIcon")
            val completeIcon = liteStore.getJSONObject("completeIcon")

            val mySticker = json.getJSONObject("MySticker")

            val orderIcon = mySticker.getJSONObject("orderIcon")
            val hideIcon = mySticker.getJSONObject("hideIcon")


            val keyboard = json.getJSONObject("Keyboard")

            keyboardNumOfColumns = Utils.getInt(keyboard, "numOfColumns", 3)

            val liteStoreIcon = keyboard.getJSONObject("liteStoreIcon")

            val storePolicy = json.getJSONObject("StorePolicy")
            allowPremium = Utils.getString(storePolicy, "allowPremium", "N")

            val price = storePolicy.getJSONObject("price")
            pngPrice = Utils.getDouble(price, "png")
            gifPrice = Utils.getDouble(price, "gif")

            val sticker = json.getJSONObject("Sticker")

            val backIcon = sticker.getJSONObject("backIcon")
            val closeIcon = sticker.getJSONObject("closeIcon")
            val downloadButtonColor = sticker.getJSONObject("downloadButtonColor")

            detailNumOfColumns = Utils.getInt(sticker, "numOfColumns")


            if (useLightMode) {
                themeColor = Utils.getString(color, LIGHT_KEY, "#ffffff")
                themeGroupedBgColor = Utils.getString(groupedBgColor, LIGHT_KEY, "#f7f8f9")
                themeContentsBgColor = Utils.getString(contentsBgColor, LIGHT_KEY, "#ffffff")

                searchbarBgColor = Utils.getString(searchbarColor, LIGHT_KEY, "#eeeeee")
                searchbarIconName = Utils.getString(searchbarIcon, LIGHT_KEY)
                searchbarDeleteIconName = Utils.getString(searchbarDeleteIcon, LIGHT_KEY)
                searchTagBgColor = Utils.getString(searchTags, LIGHT_KEY, "#ff855b")

                storeDownloadIconName = Utils.getString(downloadIcon, LIGHT_KEY)
                storeCompleteIconName = Utils.getString(completeIcon, LIGHT_KEY)

                orderIconName = Utils.getString(orderIcon, LIGHT_KEY)
                hideIconName = Utils.getString(hideIcon, LIGHT_KEY)

                keyboardStoreIconName = Utils.getString(liteStoreIcon, LIGHT_KEY)

                detailBackIconName = Utils.getString(backIcon, LIGHT_KEY)
                detailCloseIconName = Utils.getString(closeIcon, LIGHT_KEY)
                detailDownloadButtonColor = Utils.getString(downloadButtonColor, LIGHT_KEY)
            } else {
                themeColor = Utils.getString(color, DARK_KEY, "#171b1c")
                themeGroupedBgColor = Utils.getString(groupedBgColor, DARK_KEY, "#1e2427")
                themeContentsBgColor = Utils.getString(contentsBgColor, DARK_KEY, "#2e363a")

                searchbarBgColor = Utils.getString(searchbarColor, DARK_KEY, "#2e363a")
                searchbarIconName = Utils.getString(searchbarIcon, DARK_KEY)
                searchbarDeleteIconName = Utils.getString(searchbarDeleteIcon, DARK_KEY)
                searchTagBgColor = Utils.getString(searchTags, DARK_KEY, "#ff855b")

                storeDownloadIconName = Utils.getString(downloadIcon, DARK_KEY)
                storeCompleteIconName = Utils.getString(completeIcon, DARK_KEY)

                orderIconName = Utils.getString(orderIcon, DARK_KEY)
                hideIconName = Utils.getString(hideIcon, DARK_KEY)

                keyboardStoreIconName = Utils.getString(liteStoreIcon, DARK_KEY)

                detailBackIconName = Utils.getString(backIcon, DARK_KEY)
                detailCloseIconName = Utils.getString(closeIcon, DARK_KEY)
                detailDownloadButtonColor = Utils.getString(downloadButtonColor, DARK_KEY)
            }
        }

        fun getKeyboardStoreResourceId(context: Context): Int {
            return if (keyboardStoreIconName.isNotEmpty()) {
                Utils.getResource(keyboardStoreIconName, context)
            } else {
                if (useLightMode) {
                    R.mipmap.ic_store
                } else {
                    R.mipmap.ic_store_dark
                }
            }
        }

        fun getSearchbarResourceId(context: Context): Int {
            return if (searchbarIconName.isNotEmpty()) {
                Utils.getResource(searchbarIconName, context)
            } else {
                if (useLightMode) {
                    R.mipmap.icon_search
                } else {
                    R.mipmap.icon_search_dark
                }
            }
        }

        fun getEraseResourceId(context: Context): Int {
            return if (searchbarDeleteIconName.isNotEmpty()) {
                Utils.getResource(searchbarDeleteIconName, context)
            } else {
                if (useLightMode) {
                    R.mipmap.icon_erase
                } else {
                    R.mipmap.icon_erase_dark
                }
            }
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

        fun getOrderIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_move
            if (orderIconName.isNotEmpty()) {
                imageId = Utils.getResource(orderIconName, context)
            }
            return imageId
        }

        fun getAddIconResourceId(): Int {
            var imageId = R.mipmap.add_3
            if (!useLightMode) {
                imageId = R.mipmap.ic_add_dark
            }
            return imageId
        }

        fun getHideIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_hide
            if (hideIconName.isNotEmpty()) {
                imageId = Utils.getResource(hideIconName, context)
            }
            return imageId
        }

        fun getBackIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_back
            if (detailBackIconName.isNotEmpty()) {
                imageId = Utils.getResource(detailBackIconName, context)
            }
            return imageId
        }

        fun getCloseIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_close
            if (detailCloseIconName.isNotEmpty()) {
                imageId = Utils.getResource(detailCloseIconName, context)
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
            return if (selected) {
                if (!useLightMode) {
                    ContextCompat.getColor(context, R.color.c_f3f4f5)
                } else {
                    ContextCompat.getColor(context, R.color.c_374553)
                }
            } else {
                if (!useLightMode) {
                    ContextCompat.getColor(context, R.color.c_646f7c)
                } else {
                    ContextCompat.getColor(context, R.color.c_c6c8cf)
                }
            }
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

        fun getActiveStickerBackgroundColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_f8d4c7)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_ff4500)
            }
            return color
        }

        fun getHiddenStickerBackgroundColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_eaebee)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_2e363a)
            }
            return color
        }

        fun getActiveHiddenStickerTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_374553)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_ffffff)
            }
            return color
        }

        fun getMovingBackgroundColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_25292a)
            }
            return color
        }

        fun getAlertBackgroundColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_ffffff)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_4a4a4a)
            }
            return color
        }

        fun getAlertTitleTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_121212)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_d3d3d3)
            }
            return color
        }

        fun getAlertContentsTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_5f5f5f)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_e1e1e1)
            }
            return color
        }

        fun getAlertButtonTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_2d8cbf)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_5f97f6)
            }
            return color
        }

        fun getDetailPackageNameTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_000000)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            }
            return color
        }

        fun getDetailDownloadBackgroundColor(context: Context): Int {
            if (detailDownloadButtonColor.count() > 0) {
                return Color.parseColor(detailDownloadButtonColor)
            }
            var color = ContextCompat.getColor(context, R.color.c_ff774a)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_ff855b)
            }
            return color
        }

        fun getSearchTitleTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_374553)
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
            drawable.alpha = (storeTrendingOpacity * 255).roundToInt()

            return color
        }

    }
}