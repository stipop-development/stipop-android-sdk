package io.stipop.models

import io.stipop.StipopUtils
import org.json.JSONObject

class SPSticker() {

    var packageId: Int = -1
    var stickerId: Int = -1
    var stickerImg: String? = null
    var stickerImgLocalFilePath: String? = null
    var favoriteYN: String = ""
    var keyword: String = ""

    constructor(
        packageId: Int,
        stickerId: Int,
        stickerImg: String,
        favoriteYN: String,
        keyword: String,
    ) : this() {
        this.packageId = packageId
        this.stickerId = stickerId
        this.stickerImg = stickerImg
        this.favoriteYN = favoriteYN
        this.keyword = keyword
    }

    constructor(json: JSONObject) : this() {
        this.packageId = StipopUtils.getInt(json, "packageId")
        this.stickerId = StipopUtils.getInt(json, "stickerId")
        this.stickerImg = StipopUtils.getString(json, "stickerImg")
        this.favoriteYN = StipopUtils.getString(json, "favoriteYN")
        this.keyword = StipopUtils.getString(json, "keyword")
    }

}