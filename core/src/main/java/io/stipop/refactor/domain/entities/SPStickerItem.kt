package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class SPStickerItem(@SerializedName("stickerImg")
                           val stickerImg: String = "",
                         @SerializedName("keyword")
                           val keyword: String = "",
                         @SerializedName("stickerId")
                           val stickerId: Int = 0) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is SPStickerItem -> stickerId == other.stickerId
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = stickerImg.hashCode()
        result = 31 * result + keyword.hashCode()
        result = 31 * result + stickerId
        return result
    }
}
