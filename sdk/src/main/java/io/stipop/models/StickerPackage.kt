package io.stipop.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class StickerPackage(
    @SerializedName("packageId")
    val packageId: Int = -1,
    @SerializedName("packageName")
    val packageName: String? = null,
    @SerializedName("packageImg")
    val packageImg: String? = null,
    @SerializedName("packageCategory")
    val packageCategory: String? = null,
    @SerializedName("packageKeywords")
    val packageKeywords: String? = null,
    @SerializedName("packageAnimated")
    val packageAnimated: String? = null,
    @SerializedName("isView")
    var view: String? = null,
    @SerializedName("order")
    var order: Int = -1,
    @SerializedName("isNew")
    val new: String? = null,
    @SerializedName("artistName")
    val artistName: String? = null,
    @SerializedName("language")
    val language: String? = null,
    @SerializedName("isDownload")
    var download: String? = null,
    @SerializedName("isWish")
    var wish: String? = null,

    @SerializedName("cardImgUrl")
    val cardImgUrl: String? = null,
    @SerializedName("stickers")
    var stickers: ArrayList<SPSticker> = ArrayList(),
    @SerializedName("lightBackgroundCode")
    val lightBackgroundCode: String? = null,
    @SerializedName("darkBackgroundCode")
    val darkBackgroundCode: String? = null
) {
    fun getIsNew(): Boolean {
        return this.new == "Y"
    }

    fun getIsVisible(): Boolean {
        return this.view == "Y"
    }

    fun isDownloaded(): Boolean {
        return this.download == "Y"
    }

    fun toSPPackage(): SPPackage {
        return SPPackage(
            artistName = artistName ?: "",
            download = download ?: "",
            language = language ?: "",
            new = new ?: "",
            packageAnimated = packageAnimated ?: "",
            packageCategory = packageCategory ?: "",
            packageId = packageId,
            packageImg = packageImg ?: "",
            packageKeywords = packageKeywords ?: "",
            packageName = packageName ?: "",
            wish = wish ?: "",
            view = view ?: ""
        )
    }
}
