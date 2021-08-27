package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class PageMap(@SerializedName("pageCount")
                   val pageCount: Int = 0,
                   @SerializedName("startPage")
                   val startPage: Int = 0,
                   @SerializedName("pageNumber")
                   val pageNumber: Int = 0,
                   @SerializedName("startRow")
                   val startRow: Int = 0,
                   @SerializedName("onePageCountRow")
                   val onePageCountRow: Int = 0,
                   @SerializedName("pageGroupCount")
                   val pageGroupCount: Int = 0,
                   @SerializedName("endRow")
                   val endRow: Int = 0,
                   @SerializedName("totalCount")
                   val totalCount: Int = 0,
                   @SerializedName("groupNumber")
                   val groupNumber: Int = 0,
                   @SerializedName("modNum")
                   val modNum: Int = 0,
                   @SerializedName("groupCount")
                   val groupCount: Int = 0,
                   @SerializedName("listStartNumber")
                   val listStartNumber: Int = 0,
                   @SerializedName("endPage")
                   val endPage: Int = 0)
