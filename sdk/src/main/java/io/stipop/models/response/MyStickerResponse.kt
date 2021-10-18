package io.stipop.models.response

import com.google.gson.annotations.SerializedName
import io.stipop.models.StickerPackage

data class MyStickerResponse(
    @SerializedName("header") val header: ResponseHeader,
    @SerializedName("body") val body: ResponseBody,
    val nextPage: Int? = null
){
    data class ResponseBody(val packageList: List<StickerPackage>?, val pageMap: PageMapInfo)
    data class PageMapInfo(val pageNumber: Int, val onPageCountRow: Int, val totalCount: Int, val pageCount: Int, val groupCount: Int, val groupNumber: Int, val pageGroupCount: Int, val startPage: Int, val endPage: Int, val startRow: Int, val endRow: Int, val modNum: Int, val listStartNumber: Int)
}
