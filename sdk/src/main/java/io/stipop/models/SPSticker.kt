package io.stipop.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import io.stipop.StipopUtils
import io.stipop.models.enums.SPPriceTier
import org.json.JSONObject

@Keep
class SPSticker() {
    @SerializedName("packageId")
    var packageId: Int = -1
    @SerializedName("stickerId")
    var stickerId: Int = -1
    @SerializedName("stickerImg")
    var stickerImg: String? = null
    @SerializedName("stickerImgLocalFilePath")
    var stickerImgLocalFilePath: String? = null
    @SerializedName("favoriteYN")
    var favoriteYN: String = ""
    @SerializedName("keyword")
    var keyword: String = ""
    @SerializedName("isDownload")
    var isDownload: String? = ""
    @SerializedName("price")
    var price: String? = ""

    constructor(
        packageId: Int,
        stickerId: Int,
        stickerImg: String,
        favoriteYN: String,
        keyword: String?,
        isDownload: String?,
        price: String?,
    ) : this() {
        this.packageId = packageId
        this.stickerId = stickerId
        this.stickerImg = stickerImg
        this.favoriteYN = favoriteYN
        this.keyword = keyword ?: ""
        this.isDownload = isDownload ?: "N"
        this.price = price ?: ""
    }

    constructor(json: JSONObject) : this() {
        this.packageId = StipopUtils.getInt(json, "packageId")
        this.stickerId = StipopUtils.getInt(json, "stickerId")
        this.stickerImg = StipopUtils.getString(json, "stickerImg")
        this.favoriteYN = StipopUtils.getString(json, "favoriteYN")
        this.keyword = StipopUtils.getString(json, "keyword")
    }

    enum class Density(val type: String?) {
        NONE(""), STICKER_THUMB("?w=350")
    }

    fun getStickerThumbUrl(): String?{
        return "$stickerImg${Density.STICKER_THUMB}"
    }

    fun getPriceTier(): SPPriceTier? {
        return SPPriceTier.getPriceTier(this.price?.uppercase())
    }
}