package io.stipop.models.response

import com.google.gson.annotations.SerializedName
import io.stipop.models.StickerPackage

internal class StickerPackagesResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody,
    val nextPage: Int? = null
){
    data class ResponseBody(val packageList: List<StickerPackage>, val pageMap: PageMapInfo)
}