package io.stipop.sample.stipop_auth.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class GetNewAccessTokenResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody?
){
    @Keep
    data class ResponseBody(@SerializedName("accessToken") val accessToken: String?)
}