package io.stipop.models

data class Sticker(
    val packageId: Int = -1,
    val stickerId: Int = -1,
    var stickerImg: String? = null,
    var stickerImgLocalFilePath: String? = null,
    var favoriteYN: String = "",
    var keyword: String = "",
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
        fun fromSpSticker(spSticker: SPSticker) : Sticker{
            return Sticker(spSticker.packageId, spSticker.stickerId, spSticker.stickerImg, spSticker.stickerImgLocalFilePath, spSticker.favoriteYN, spSticker.keyword)
        }
    }
}