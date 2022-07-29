package io.stipop.sample.stipop_auth.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class GetAccessTokenAPIBody(
    @SerializedName("appId") val appId: String? = "YOUR_APP_ID",
    @SerializedName("userId") val userId: String? = "YOUR_APP_USER_ID",
    @SerializedName("clientId") val clientId: String? = "YOUR_APP_CLIENT_ID",
    @SerializedName("clientSecret") val clientSecret: String? = "YOUR_APP_CLIENT_SECRET",
    @SerializedName("refreshToken") val refreshToken: String? = "YOUR_APP_REFRESH_TOKEN",
    @SerializedName("expiryTime") val expiryTime: Int? = 86400
)