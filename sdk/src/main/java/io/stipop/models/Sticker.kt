package io.stipop.models

data class Sticker(
    var packageId: Int = -1,
    var stickerId: Int = -1,
    var stickerImg: String? = null,
    var stickerImgLocalFilePath: String? = null,
    var favoriteYN: String = "",
    var keyword: String = "",
)