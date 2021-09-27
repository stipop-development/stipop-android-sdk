package io.stipop.models

import io.stipop.Utils
import org.json.JSONObject

class SPSticker() {

    var packageId: Int = -1
    var stickerId: Int = -1
    var stickerImg: String? = null
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
        this.packageId = Utils.getInt(json, "packageId")
        this.stickerId = Utils.getInt(json, "stickerId")
        this.stickerImg = Utils.getString(json, "stickerImg")
        this.favoriteYN = Utils.getString(json, "favoriteYN")
        this.keyword = Utils.getString(json, "keyword")
    }

}