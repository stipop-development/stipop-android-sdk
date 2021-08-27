package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class StickerListBody(
    @SerializedName("stickerList")
    val stickerList: List<StickerItem>?,
    @SerializedName("pageMap")
    val pageMap: PageMap
)
