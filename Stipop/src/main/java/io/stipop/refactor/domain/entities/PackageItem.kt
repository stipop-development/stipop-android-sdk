package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class PackageItem(@SerializedName("packageCategory")
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
                           val order: Int = 0)
