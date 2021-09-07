package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class SPStickerListResponse(@SerializedName("header")
                                 override val header: SPHeader,
                                 @SerializedName("body")
                                 override val body: SPStickerListBody) : SPResponse<SPStickerListBody>
