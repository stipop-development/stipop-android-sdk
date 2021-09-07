package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class SPStickerListBody(
    @SerializedName("stickerList")
    val stickerList: List<SPStickerItem>?,
    @SerializedName("pageMap")
    val pageMap: SPPageMap
)
