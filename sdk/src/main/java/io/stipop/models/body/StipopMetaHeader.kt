package io.stipop.models.body

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class StipopMetaHeader(
    @SerializedName("platform") val platform: String? = "android-sdk",
    @SerializedName("sdk_version") val sdk_version: String? = null,
    @SerializedName("os_version") val os_version: String? = null
)
