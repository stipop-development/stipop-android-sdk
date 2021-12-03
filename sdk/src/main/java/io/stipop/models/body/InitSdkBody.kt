package io.stipop.models.body

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class InitSdkBody(
    @SerializedName("userId") val userId: String? = "",
    @SerializedName("lang") val lang: String? = ""
)
