package io.stipop.models.response

import com.google.gson.annotations.SerializedName

internal data class KeywordListResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody
) {
    data class ResponseBody(val keywordList: List<KeywordSet>)
    data class KeywordSet(val keyword: String)
}
