package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class VoidResponse(
    @SerializedName("header")
    val header: Header
)


