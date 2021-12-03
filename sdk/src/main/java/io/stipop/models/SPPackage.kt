package io.stipop.models

import androidx.annotation.Keep
import io.stipop.StipopUtils
import org.json.JSONObject

@Keep
class SPPackage() {

    var artistName: String? = null
    var download: String? = null
    var language: String? = null
    var new: String? = null
    var packageAnimated: String? = null
    var packageCategory: String? = null
    var packageId: Int = -1
    var packageImg: String? = null
    var packageKeywords: String? = null
    var packageName: String? = null
    var wish: String? = null
    var view: String? = null
    var order: Int = -1
    var stickers = ArrayList<SPSticker>()
    val isDownload: Boolean
        get() = this.download == "Y"
    val isView: Boolean
        get() = this.view == "Y"

    constructor(
        packageId: Int
    ) : this() {
        this.packageId = packageId
    }

    constructor(
        artistName: String,
        download: String,
        language: String,
        new: String,
        packageAnimated: String,
        packageCategory: String,
        packageId: Int,
        packageImg: String,
        packageKeywords: String,
        packageName: String,
        wish: String,
        view: String
    ) : this() {
        this.artistName = artistName
        this.download = download
        this.language = language
        this.new = new
        this.packageAnimated = packageAnimated
        this.packageCategory = packageCategory
        this.packageId = packageId
        this.packageImg = packageImg
        this.packageKeywords = packageKeywords
        this.packageName = packageName
        this.wish = wish
        this.view = view
    }

    constructor(json: JSONObject) : this() {
        this.packageId = StipopUtils.getInt(json, "packageId")
        this.packageName = StipopUtils.getString(json, "packageName")
        this.packageImg = StipopUtils.getString(json, "packageImg")
        this.packageCategory = StipopUtils.getString(json, "packageCategory")
        this.packageKeywords = StipopUtils.getString(json, "packageKeywords")
        this.packageAnimated = StipopUtils.getString(json, "packageAnimated")
        this.new = StipopUtils.getString(json, "isNew")
        this.artistName = StipopUtils.getString(json, "artistName")
        this.language = StipopUtils.getString(json, "language")
        this.download = StipopUtils.getString(json, "isDownload")
        this.wish = StipopUtils.getString(json, "isWish")
        this.view = StipopUtils.getString(json, "isView")
        this.order = StipopUtils.getInt(json, "order")

        if (!json.isNull("stickers")) {
            val stickers = json.getJSONArray("stickers")
            for (i in 0 until stickers.length()) {
                this.stickers.add(SPSticker(stickers.get(i) as JSONObject))
            }
        }
    }

    override fun toString(): String {
//        return "[packageId: $packageId, packageName: $packageName, packageImg: $packageImg, packageCategory: $packageCategory, packageKeywords: $packageKeywords, packageAnimated: $packageAnimated, new: $new, artistName: $artistName, language: $language, download: $download, wish: $wish, view: $view, order: $order]"
        return "[packageId: $packageId, order: $order]"
    }

}