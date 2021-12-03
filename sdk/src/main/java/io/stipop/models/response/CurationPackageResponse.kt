package io.stipop.models.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import io.stipop.models.CuratedCard

@Keep
internal class CurationPackageResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody,
) {
    @Keep
    data class ResponseBody(
        @SerializedName("card") val card: CuratedCard,
        @SerializedName("pageMap") val pageMap: PageMapInfo? = null
    )
}