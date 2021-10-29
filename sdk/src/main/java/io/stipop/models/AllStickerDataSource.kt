package io.stipop.models

internal data class AllStickerDataSource(
    val trendingList: ArrayList<StickerPackage> = ArrayList(),
    val defaultList: ArrayList<StickerPackage> = ArrayList()
)