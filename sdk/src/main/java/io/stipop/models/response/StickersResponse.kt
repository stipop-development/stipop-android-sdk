package io.stipop.models.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import io.stipop.models.Sticker

@Keep
internal class StickersResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody?
) {
    @Keep
    data class ResponseBody(
        @SerializedName("stickerList") val stickerList: List<Sticker>? = emptyList(),
        @SerializedName("pageMap") val pageMap: PageMapInfo
    )
}