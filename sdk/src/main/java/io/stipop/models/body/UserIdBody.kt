package io.stipop.models.body

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import io.stipop.Stipop

@Keep
internal data class UserIdBody(@SerializedName("userId") val userId: String? = Stipop.userId)