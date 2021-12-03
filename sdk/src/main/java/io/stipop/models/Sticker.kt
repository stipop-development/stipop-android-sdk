package io.stipop.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Sticker(
    @SerializedName("packageId") val packageId: Int = -1,
    @SerializedName("stickerId") val stickerId: Int = -1,
    @SerializedName("stickerImg") var stickerImg: String? = null,
    @SerializedName("stickerImgLocalFilePath") var stickerImgLocalFilePath: String? = null,
    @SerializedName("favoriteYN") var favoriteYN: String = "",
    @SerializedName("keyword") var keyword: String = "",
) {
    fun toSPSticker(): SPSticker {
        return SPSticker(
            packageId = packageId,
            stickerId = stickerId,
            stickerImg = stickerImg ?: "",
            favoriteYN = favoriteYN,
            keyword = keyword,
        )
    }

    companion object {
        fun fromSpSticker(spSticker: SPSticker): Sticker {
            return Sticker(
                spSticker.packageId,
                spSticker.stickerId,
                spSticker.stickerImg,
                spSticker.stickerImgLocalFilePath,
                spSticker.favoriteYN,
                spSticker.keyword
            )
        }
    }
}