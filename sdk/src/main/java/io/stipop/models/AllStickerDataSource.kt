package io.stipop.models

data class AllStickerDataSource(
    val trendingList: ArrayList<StickerPackage> = ArrayList(),
    val defaultList: ArrayList<StickerPackage> = ArrayList()
)