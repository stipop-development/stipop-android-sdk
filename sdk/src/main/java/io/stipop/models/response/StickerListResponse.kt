package io.stipop.models.response

import com.google.gson.annotations.SerializedName
import io.stipop.models.Sticker

internal data class StickerListResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody
) {
    data class ResponseBody(val stickerList: List<Sticker>?=emptyList(), val pageMap: PageMapInfo)
}