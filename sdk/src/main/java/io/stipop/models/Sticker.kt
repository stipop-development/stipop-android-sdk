package io.stipop.models

data class Sticker(
    val packageId: Int = -1,
    val stickerId: Int = -1,
    var stickerImg: String? = null,
    var stickerImgLocalFilePath: String? = null,
    var favoriteYN: String = "",
    var keyword: String = "",
)