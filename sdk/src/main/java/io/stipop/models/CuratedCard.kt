package io.stipop.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class CuratedCard(
    @SerializedName("cardId") val cardId: Int,
    @SerializedName("title") val cardTitle: String="",
    @SerializedName("type") val type: String,
    @SerializedName("imgUrl") val String: String?=null,
    @SerializedName("language") val language: String,
    @SerializedName("country") val country: String,
    @SerializedName("packageList") val packageList: List<StickerPackage>,
)