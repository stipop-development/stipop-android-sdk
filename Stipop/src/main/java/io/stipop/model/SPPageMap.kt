package io.stipop.model

data class SPPageMap(
    val pageNumber: Int,
    val onePageCountRow: Int,
    val totalCount: Int,
    val pageCount: Int,
    val groupCount: Int,
    val groupNumber: Int,
    val pageGroupCount: Int,
    val startPage: Int,
    val endPage: Int,
    val startRow: Int,
    val endRow: Int,
    val modNum: Int,
    val listStartNumber: Int,
)