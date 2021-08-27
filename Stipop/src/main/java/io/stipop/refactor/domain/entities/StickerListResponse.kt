package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class StickerListResponse(@SerializedName("header")
                               val header: Header,
                               @SerializedName("body")
                               val body: StickerListBody)
