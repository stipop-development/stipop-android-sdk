package io.stipop.models.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ResponseHeader(
    @SerializedName("code") val code: String,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
) {
    fun isSuccess(): Boolean {
        return code == "200" || status == "success"
    }
}
