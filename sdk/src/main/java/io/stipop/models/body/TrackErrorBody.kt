package io.stipop.models.body

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class TrackErrorBody(@SerializedName("error") val error: String?)