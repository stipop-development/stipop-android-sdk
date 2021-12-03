package io.stipop.models.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class StipopResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: Any? = null
)