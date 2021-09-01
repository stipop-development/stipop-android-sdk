package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.domain.entities.*

interface SearchRepository {

    val searchKeywordList: Observable<List<SPKeywordItem>>
    val searchStickerList: Observable<List<SPStickerItem>>

    suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String? = "en",
        countryCode: String? = "US",
        limit: Int? = 20,
        pageNumber: Int? = 1
    ): SPStickerListResponse

    suspend fun trendingSearchTerms(
        apikey: String,
        userId: String,
        lang: String? = "en",
        countryCode: String? = "US",
        limit: Int? = 20,
    ): SPKeywordListResponse

    suspend fun recentSearch(
        apikey: String,
        userId: String,
    ): SPKeywordListResponse

    fun onLoadSearchKeywordList(apikey: String, userId: String)
    fun onLoadSearchStickerList(apikey: String,
                                userId: String,
                                keyword: String,
                                lang: String? = "en",
                                countryCode: String? = "US",
                                limit: Int? = 20,
                                pageNumber: Int? = 1)
}
