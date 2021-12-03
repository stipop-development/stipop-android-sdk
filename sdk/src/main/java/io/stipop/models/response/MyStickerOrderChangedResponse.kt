package io.stipop.models.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import io.stipop.models.StickerPackage

@Keep
internal data class MyStickerOrderChangedResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody
) {
    @Keep
    data class ResponseBody(@SerializedName("packageList") val packageList: List<StickerPackage>)
}
