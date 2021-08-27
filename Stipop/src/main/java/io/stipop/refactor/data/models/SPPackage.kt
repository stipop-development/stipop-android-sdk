package io.stipop.refactor.data.models

import io.stipop.Utils
import io.stipop.refactor.domain.entities.PackageItem
import org.json.JSONObject

class SPPackage() {

    companion object {

        fun fromEntity(entity: PackageItem): SPPackage {
            val _artistName: String = entity.artistName
            val _download: String = entity.isDownload
            val _language: String = entity.language
            val _new: String = "N"
            val _packageAnimated: String = entity.packageAnimated
            val _packageCategory: String = entity.packageCategory
            val _packageId: Int = entity.packageId
            val _packageImg: String = entity.packageImg
            val _packageKeywords: String = entity.packageKeywords
            val _packageName: String = entity.packageName
            val _wish: String = "N"
            val _view: String = "N"

            return SPPackage(
                _artistName,
                _download,
                _language,
                _new,
                _packageAnimated,
                _packageCategory,
                _packageId,
                _packageImg,
                _packageKeywords,
                _packageName,
                _wish,
                _view,
            )
        }

    }

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
        this.packageId = Utils.getInt(json, "packageId")
        this.packageName = Utils.getString(json, "packageName")
        this.packageImg = Utils.getString(json, "packageImg")
        this.packageCategory = Utils.getString(json, "packageCategory")
        this.packageKeywords = Utils.getString(json, "packageKeywords")
        this.packageAnimated = Utils.getString(json, "packageAnimated")
        this.new = Utils.getString(json, "isNew")
        this.artistName = Utils.getString(json, "artistName")
        this.language = Utils.getString(json, "language")
        this.download = Utils.getString(json, "isDownload")
        this.wish = Utils.getString(json, "isWish")
        this.view = Utils.getString(json, "isView")
        this.order = Utils.getInt(json, "order")

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

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is SPPackage -> packageId == other.packageId
            else -> false
        }
    }

}
