package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class SPPackageItem(@SerializedName("packageCategory")
                           val packageCategory: String = "",
                         @SerializedName("packageKeywords")
                           val packageKeywords: String = "",
                         @SerializedName("isDownload")
                           val isDownload: String = "",
                         @SerializedName("packageImg")
                           val packageImg: String = "",
                         @SerializedName("isView")
                           val isView: String = "",
                         @SerializedName("packageId")
                           val packageId: Int = 0,
                         @SerializedName("language")
                           val language: String = "",
                         @SerializedName("isNew")
                           val isNew: String = "",
                         @SerializedName("packageAnimated")
                           val packageAnimated: String = "",
                         @SerializedName("artistName")
                           val artistName: String = "",
                         @SerializedName("packageName")
                           val packageName: String = "",
                         @SerializedName("isWish")
                           val isWish: String = "",
                         @SerializedName("order")
                           val order: Int = 0,
                         @SerializedName("stickers")
                           val stickers: List<SPStickerItem> = listOf()) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is SPPackageItem -> packageId == other.packageId
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = packageCategory.hashCode()
        result = 31 * result + packageKeywords.hashCode()
        result = 31 * result + isDownload.hashCode()
        result = 31 * result + packageImg.hashCode()
        result = 31 * result + isView.hashCode()
        result = 31 * result + packageId
        result = 31 * result + language.hashCode()
        result = 31 * result + isNew.hashCode()
        result = 31 * result + packageAnimated.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + isWish.hashCode()
        result = 31 * result + order
        result = 31 * result + stickers.hashCode()
        return result
    }
}
