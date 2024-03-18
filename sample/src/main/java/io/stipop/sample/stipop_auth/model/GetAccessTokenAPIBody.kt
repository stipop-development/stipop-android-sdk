package io.stipop.sample.stipop_auth.model

import com.google.gson.annotations.SerializedName

internal data class GetAccessTokenAPIBody(
    @SerializedName("appId") val appId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("clientId") val clientId: String,
    @SerializedName("clientSecret") val clientSecret: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiryTime") val expiryTime: Int
)