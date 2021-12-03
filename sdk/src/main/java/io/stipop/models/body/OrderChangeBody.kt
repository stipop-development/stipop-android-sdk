package io.stipop.models.body

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class OrderChangeBody(
    @SerializedName("currentOrder") val currentOrder: Int,
    @SerializedName("newOrder") val newOrder: Int
)