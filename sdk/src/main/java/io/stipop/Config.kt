package io.stipop

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import io.stipop.models.StipopConfigData
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.math.roundToInt

internal object Config {

    var stipopConfigData: StipopConfigData = StipopConfigData()

    private val resourceLists = arrayListOf(
        R.mipmap.ic_sticker_border_1,
        R.mipmap.ic_sticker_border_2,
        R.mipmap.ic_sticker_border_3,
        R.mipmap.ic_add_border_1,
        R.mipmap.ic_add_border_2,
        R.mipmap.ic_add_border_3,
        R.mipmap.ic_back_border_1,
        R.mipmap.ic_back_border_2,
        R.mipmap.ic_back_border_3,
        R.mipmap.ic_cancel,
        R.mipmap.ic_close_border_1,
        R.mipmap.ic_close_border_2,
        R.mipmap.ic_close_border_3,
        R.mipmap.ic_store,
        R.mipmap.ic_store_dark
    )

    object FontStyle {
        var fontFamily = "system"
        var fontWeight = 400
        var fontCharacter: Float = 0.0f
        var fontFace: Typeface? = null
    }

    const val baseUrl = "https://messenger.stipop.io/v1"

    private var stickerIconNormalName = "ic_sticker_border_3"
    var themeUseLightMode = true
    var themeBackgroundColor = "#ffffff"
    var themeGroupedContentBackgroundColor = "#f7f8f9"
    var themeMainColor = "#FF5D1E"
    var themeIconColor = "#414141"
    var themeIconTintColor = "#FF5D1E"
    var searchbarRadius = 10
    var searchNumOfColumns = 3
    var searchTagsHidden = false
    private var searchbarIconName = "ic_sticker_border_3"
    private var searchbarDeleteIconName = "ic_erase_border_3"
    var storeListType = ""
    private var storeTrendingUseBackgroundColor = false
    private var storeTrendingBackgroundColor = "#EEEEEE"
    private var storeTrendingOpacity = 0.0
    private var storeDownloadIconName = "ic_download_border_3"
    private var storeCompleteIconName = "ic_downloaded_border_3"
    var storeRecommendedTagShow = false
    private var orderIconName = "ic_move_border_3"
    private var hideIconName = "ic_hide_border_3"
    private var keyboardStoreIconName = ""
    var keyboardNumOfColumns = 3
    var stickerDoubleTap = false
    var pickerViewSearchIsActive = false
    var pickerViewSettingIsActive = true
    var pickerViewStoreIsActive = true
    private var viewPickerViewType = "PopupWindow"
    var pickerViewLayoutOnKeyboard = true
    var isPackPurchaseMode = false
    var sAuthIsActive = false
    private var detailBackIconName = "ic_back_border_3"
    private var detailCloseIconName = "ic_close_border_3"
    var detailNumOfColumns = 3
    var showPreview = false
    var previewPadding = 0
    private var previewFavoritesOnIconName = ""
    private var previewFavoritesOffIconName = ""
    private var previewCloseIconName = ""

    internal fun configure(context: Context, configFileName: String? = null, callback: ((isSuccess: Boolean) -> Unit)) {
        val jsonString = getJsonDataFromAsset(context, configFileName ?: Constants.KEY.ASSET_NAME) ?: return
        try {
            val json = JSONObject(jsonString)
            stipopConfigData = Gson().fromJson(jsonString, StipopConfigData::class.java)
            parse(context, json)
            callback(true)
            Log.d("STIPOP-SDK", "Stipop.json configuration completed.")
        } catch (exception: JSONException) {
            exception.printStackTrace()
            callback(false)
            Log.e(
                "STIPOP-SDK",
                "Stipop.json configuration failed.\nPlease check it is in 'assets' folder."
            )
        }
    }

    private fun getJsonDataFromAsset(context: Context, confieFileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(confieFileName).bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    private fun parse(context: Context, json: JSONObject) {
        stickerIconNormalName = stipopConfigData.spvIcon

        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> themeUseLightMode = true
            AppCompatDelegate.MODE_NIGHT_YES -> themeUseLightMode = false
            else -> {
                val deviceDarkMode = context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)

                when (deviceDarkMode) {
                    Configuration.UI_MODE_NIGHT_YES -> themeUseLightMode = false

                    Configuration.UI_MODE_NIGHT_NO -> themeUseLightMode = true
                }
            }
        }


        with(stipopConfigData) {

            theme.let {
                themeBackgroundColor = it.backgroundColor.getDayNightKey(themeUseLightMode)
                themeGroupedContentBackgroundColor = it.groupedContentBackgroundColor.getDayNightKey(themeUseLightMode)
                themeMainColor = it.mainColor.getDayNightKey(themeUseLightMode)
                themeIconColor = it.iconColor.normalColor.getDayNightKey(themeUseLightMode)
                themeIconTintColor = it.iconColor.tintColor.getDayNightKey(themeUseLightMode)

                FontStyle.fontFamily = it.font.family
                FontStyle.fontWeight = it.font.weight
                FontStyle.fontCharacter = it.font.character
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val builder = Typeface.Builder(context.assets, FontStyle.fontFamily)
                        builder.setFontVariationSettings("'wght' ${FontStyle.fontWeight}, 'slnt' 20, 'ital' 0")
                        builder.setWeight(FontStyle.fontWeight) // Tell the system that this is a bold font.
                        FontStyle.fontFace = builder.build()
                    } else {
                        return@let
                    }
                } catch (exception: Exception) {
                    Log.e("StipopSDK", "${exception.message}")
                }
            }

            previewConfig.let {
                showPreview = it.preview
                previewPadding = it.previewPadding
                previewFavoritesOnIconName = it.favoritesOnIcon.getDayNightKey(themeUseLightMode)
                previewFavoritesOffIconName = it.favoritesOffIcon.getDayNightKey(themeUseLightMode)
                previewCloseIconName = it.closeIcon.getDayNightKey(themeUseLightMode)
            }

            searchConfig.let {
                searchbarRadius = it.searchbarRadius
                searchNumOfColumns = it.columnCount
                searchbarIconName = it.searchBarIcon
                searchbarDeleteIconName = it.searchbarDeleteIcon
                searchTagsHidden = it.visibleSearchTags.isHidden
            }

            liteStoreConfig.let {
                storeListType = it.listType
                storeDownloadIconName = it.downloadIcon
                storeCompleteIconName = it.completeIcon
                storeRecommendedTagShow = it.isRecommendTagShow()

                storeTrendingUseBackgroundColor = it.trendingConfig.isUseBackgroundColor
                storeTrendingBackgroundColor = it.trendingConfig.backgroundColor
                storeTrendingOpacity = it.trendingConfig.opacity
            }

            myStickerConfig.let {
                orderIconName = it.orderIcon
                hideIconName = it.hideIcon
            }

            stickerConfig.let {
                detailBackIconName = it.backIcon
                detailCloseIconName = it.closeIcon
                detailNumOfColumns = it.columnCount
            }

            keyboardConfig.let {
                keyboardNumOfColumns = it.columnCount
                keyboardStoreIconName = it.liteStoreIcon
            }

            functionConfig.let {
                stickerDoubleTap = it.sticker.doubleTap
            }
            iconConfig.let {
                pickerViewSearchIsActive = it.pickerView.search.isActive
                pickerViewSettingIsActive = it.pickerView.setting.isActive
                pickerViewStoreIsActive = it.pickerView.store.isActive
            }
            viewConfig.let {
                viewPickerViewType = if(it.pickerView.type == "PopupWindow" || it.pickerView.type == "Fragment") (it.pickerView.type) else "PopupWindow"
            }
            layoutConfig.let {
                pickerViewLayoutOnKeyboard = it.pickerView.onKeyboard
            }
            modeConfig.let {
                isPackPurchaseMode = it.packPurchase
            }
            sAuthConfig.let {
                sAuthIsActive = it.isActive
            }
            Log.d("StipopSdk", "Resources Size : ${resourceLists.size}")
        }
    }

    fun getStickerIconResourceId(context: Context): Int {
        return if (stickerIconNormalName.isNotEmpty()) {
            StipopUtils.getResource(stickerIconNormalName, context)
        } else {
            R.mipmap.ic_sticker_border_3
        }
    }

    fun getKeyboardStoreResourceId(context: Context): Int {
        return if (keyboardStoreIconName.isNotEmpty()) {
            StipopUtils.getResource(keyboardStoreIconName, context)
        } else {
            R.mipmap.ic_store
        }
    }

    fun getSearchbarResourceId(context: Context): Int {
        return if (searchbarIconName.isNotEmpty()) {
            StipopUtils.getResource(searchbarIconName, context)
        } else {
            R.mipmap.icon_search
        }
    }

    fun getEraseResourceId(context: Context): Int {
        return if (searchbarDeleteIconName.isNotEmpty()) {
            StipopUtils.getResource(searchbarDeleteIconName, context)
        } else {
            R.mipmap.ic_erase_border_3
        }
    }

    fun getDownloadIconResourceId(context: Context): Int {
        var imageId = R.mipmap.ic_download_border_3
        if (storeDownloadIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(storeDownloadIconName, context)
        }
        return imageId
    }

    fun getCompleteIconResourceId(context: Context): Int {
        var imageId = R.mipmap.ic_downloaded_border_3
        if (storeCompleteIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(storeCompleteIconName, context)
        }
        return imageId
    }

    fun getOrderIconResourceId(context: Context): Int {
        var imageId = R.mipmap.ic_move_border_3
        if (orderIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(orderIconName, context)
        }
        return imageId
    }

    fun getAddIconResourceId(): Int {
//            var imageId = R.mipmap.ic_add_border_3
//            if (!useLightMode) {
//                imageId = R.mipmap.ic_add_dark
//            }
        return R.mipmap.ic_add_border_3
    }

    fun getHideIconResourceId(context: Context): Int {
        var imageId = R.mipmap.ic_hide_border_3
        if (hideIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(hideIconName, context)
        }
        return imageId
    }

    fun getBackIconResourceId(context: Context): Int {
        var imageId = R.mipmap.ic_back_border_3
        if (detailBackIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(detailBackIconName, context)
        }
        return imageId
    }

    fun getCloseIconResourceId(context: Context): Int {
        var imageId = R.mipmap.ic_close_border_3
        if (detailCloseIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(detailCloseIconName, context)
        }
        return imageId
    }

    fun getPreviewFavoriteResourceId(context: Context, favorite: Boolean): Int {
        var imageId = R.mipmap.ic_favorites_off
        if (previewFavoritesOffIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(previewFavoritesOffIconName, context)
        }
        if (favorite) {
            imageId = R.mipmap.ic_favorites_on
            if (previewFavoritesOnIconName.isNotEmpty()) {
                imageId = StipopUtils.getResource(previewFavoritesOnIconName, context)
            }
        }
        return imageId
    }

    fun getPreviewCloseResourceId(context: Context): Int {
        var imageId = R.mipmap.ic_cancel
        if (previewCloseIconName.isNotEmpty()) {
            imageId = StipopUtils.getResource(previewCloseIconName, context)
        }
        return imageId
    }

    fun getErrorImage(): Int {
        var imageId = R.mipmap.error
        if (!themeUseLightMode) {
            imageId = R.mipmap.error_dark
        }
        return imageId
    }

    fun getSelectableTextColor(context: Context, selected: Boolean): Int {
        return if (selected) {
            if (!themeUseLightMode) {
                ContextCompat.getColor(context, R.color.sp_f3f4f5)
            } else {
                ContextCompat.getColor(context, R.color.sp_374553)
            }
        } else {
            if (!themeUseLightMode) {
                ContextCompat.getColor(context, R.color.sp_646f7c)
            } else {
                ContextCompat.getColor(context, R.color.sp_c6c8cf)
            }
        }
    }

    fun getTitleTextColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_646f7c)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_c6c8cf)
        }
        return color
    }

    fun getAllStickerPackageNameTextColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_374553)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_f7f8f9)
        }
        return color
    }

    fun getMyStickerHiddenPackageNameTextColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_black)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_black)
        }
        return color
    }

    fun getMyStickerHiddenArtistNameTextColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_8f8f8f)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_646f7c)
        }
        return color
    }

    fun getActiveStickerBackgroundColor(context: Context): Int {
        var color = Color.parseColor(themeMainColor)
        if (themeUseLightMode) {
            val mainColor = themeMainColor.replace("#", "")

            color = Color.parseColor("#33$mainColor")
        }
        return color
    }

    fun getHiddenStickerBackgroundColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_eaebee)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_2e363a)
        }
        return color
    }

    fun getActiveHiddenStickerTextColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_374553)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_white)
        }
        return color
    }

    fun getDetailPackageNameTextColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_black)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_f7f8f9)
        }
        return color
    }

    fun getSearchTitleTextColor(context: Context): Int {
        var color = ContextCompat.getColor(context, R.color.sp_374553)
        if (!themeUseLightMode) {
            color = ContextCompat.getColor(context, R.color.sp_c6c8cf)
        }
        return color
    }

    fun setStoreTrendingBackground(context: Context, drawable: GradientDrawable): Int {
        var color = ContextCompat.getColor(context, R.color.sp_eeeeee)

        if (storeTrendingUseBackgroundColor) {
            color = Color.parseColor(storeTrendingBackgroundColor)
        }

        drawable.setColor(color)
        drawable.alpha = (storeTrendingOpacity * 255).roundToInt()

        return color
    }

    internal fun getViewPickerViewType(): ViewPickerViewType{
        when(viewPickerViewType){
            "PopupWindow" -> return ViewPickerViewType.POPUP_WINDOW
            "Fragment" -> return ViewPickerViewType.FRAGMENT
            else -> return ViewPickerViewType.POPUP_WINDOW
        }
    }

    internal fun isPickerViewPopupWindow(): Boolean{
        return getViewPickerViewType() == ViewPickerViewType.POPUP_WINDOW
    }


}
internal enum class ViewPickerViewType{
    POPUP_WINDOW,
    FRAGMENT
}