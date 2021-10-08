package io.stipop.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class StickerPackage(
    @SerializedName("packageId")
    var packageId: Int = -1,
    @SerializedName("artistName")
    var artistName: String? = null,
    @SerializedName("isDownload")
    var download: String? = null,
    @SerializedName("language")
    var language: String? = null,
    @SerializedName("isNew")
    var new: String? = null,
    @SerializedName("packageAnimated")
    var packageAnimated: String? = null,
    @SerializedName("packageCategory")
    var packageCategory: String? = null,
    @SerializedName("packageImg")
    var packageImg: String? = null,
    @SerializedName("packageKeywords")
    var packageKeywords: String? = null,
    @SerializedName("packageName")
    var packageName: String? = null,
    @SerializedName("isWish")
    var wish: String? = null,
    @SerializedName("isView")
    var view: String? = null,
    @SerializedName("order")
    var order: Int = -1,
    @SerializedName("stickers")
    var stickers: ArrayList<SPSticker> = ArrayList()
){
    fun getIsVisible(): Boolean {
        return this.view == "Y"
    }

    fun isDownloaded(): Boolean{
        return this.download == "Y"
    }

    fun toSPPackage(): SPPackage{
        return SPPackage(
            artistName = artistName?:"",
            download = download?:"",
            language = language?:"",
            new = new?:"",
            packageAnimated = packageAnimated?:"",
            packageCategory = packageCategory?:"",
            packageId = packageId,
            packageImg = packageImg?:"",
            packageKeywords = packageKeywords?:"",
            packageName = packageName?:"",
            wish = wish?:"",
            view = view?:""
        )
    }
}
