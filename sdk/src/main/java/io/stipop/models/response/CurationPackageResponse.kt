package io.stipop.models.response

import com.google.gson.annotations.SerializedName
import io.stipop.models.CuratedCard

internal class CurationPackageResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody,
) {
    data class ResponseBody(val card: CuratedCard, val pageMap: PageMapInfo? = null)
}