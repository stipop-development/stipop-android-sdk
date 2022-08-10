package io.stipop.sample.stipop_auth.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

internal data class GetNewAccessTokenResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody?
){
    data class ResponseBody(@SerializedName("accessToken") val accessToken: String?)
}