package io.stipop.models.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class KeywordListResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody
) {
    @Keep
    data class ResponseBody(@SerializedName("keywordList") val keywordList: List<KeywordSet>)
    @Keep
    data class KeywordSet(@SerializedName("keyword") val keyword: String)
}
