package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class SPStickerItem(@SerializedName("stickerImg")
                           val stickerImg: String = "",
                         @SerializedName("keyword")
                           val keyword: String = "",
                         @SerializedName("stickerId")
                           val stickerId: Int = 0)