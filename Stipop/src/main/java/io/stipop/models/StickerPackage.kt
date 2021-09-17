package io.stipop.models

import com.google.gson.annotations.SerializedName

data class StickerPackage(
    @SerializedName("packageId")
    var packageId: Int = -1,
    @SerializedName("artistName")
    var artistName: String? = null,
    @SerializedName("download")
    var download: String? = null,
    @SerializedName("language")
    var language: String? = null,
    @SerializedName("new")
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
    @SerializedName("wish")
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

    fun getIsDownloaded(): Boolean{
        return this.download == "Y"
    }
}
