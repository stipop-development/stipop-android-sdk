package io.stipop.models.response

import com.google.gson.annotations.SerializedName
import io.stipop.models.StickerPackage

internal data class StickerPackageResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody?
){
    data class ResponseBody(@SerializedName("package") val stickerPackage: StickerPackage?)
}