package io.stipop.sample.stipop_auth.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

internal data class ResponseHeader(
    @SerializedName("code") val code: String,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)