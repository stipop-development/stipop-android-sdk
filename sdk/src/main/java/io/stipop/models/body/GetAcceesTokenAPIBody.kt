package io.stipop.models.body

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import io.stipop.Config
import io.stipop.Stipop

@Keep
internal data class GetAcceesTokenAPIBody(
    @SerializedName("appId") val appId: String? = Config.sAuthAppId,
    @SerializedName("userId") val userId: String? = Stipop.userId,
    @SerializedName("clientId") val clientId: String? = Config.sAuthClientId,
    @SerializedName("clientSecret") val clientSecret: String? = Config.sAuthClientSecret,
    @SerializedName("refreshToken") val refreshToken: String? = Config.sAuthRefreshToken,
    @SerializedName("expiryTime") val expiryTime: Int? = Config.sAuthExpiryTimeSeconds
)