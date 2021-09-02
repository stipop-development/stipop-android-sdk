package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPStickerItem

interface SearchRepository {

    val recentSearchKeywordList: Observable<List<SPKeywordItem>>
    val searchKeywordList: Observable<List<SPKeywordItem>>
    val searchStickerList: Observable<List<SPStickerItem>>
    val searchStickerListPageMap: Observable<SPPageMap>

    fun onLoadSearchKeywordList(
        apikey: String,
        userId: String,
        language: String? = "en",
        countryCode: String? = "US",
        limit: Int? = 20,
    )

    fun onLoadRecentSearchKeywordList(apikey: String, userId: String)
    fun onLoadSearchStickerList(
        apikey: String,
        userId: String,
        keyword: String,
        lang: String? = "en",
        countryCode: String? = "US",
        limit: Int? = 20,
        pageNumber: Int? = 1
    )
}
