package io.stipop.model

import io.stipop.Utils
import org.json.JSONArray
import org.json.JSONObject

class SPSticker() {

    var packageId: Int = -1
    var stickerId: Int = -1
    var stickerImg: String? = null
    var favoriteYN: String = ""

    constructor(
        packageId: Int,
        stickerId: Int,
        stickerImg: String,
    ) : this() {
        this.packageId = packageId
        this.stickerId = stickerId
        this.stickerImg = stickerImg
    }

    constructor(json: JSONObject) : this() {
        this.packageId = Utils.getInt(json, "packageId")
        this.stickerId = Utils.getInt(json, "stickerId")
        this.stickerImg = Utils.getString(json, "stickerImg")
    }

}