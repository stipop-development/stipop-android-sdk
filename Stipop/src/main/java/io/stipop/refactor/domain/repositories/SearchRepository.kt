package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPStickerItem

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
    ): SPPackageListResponse

    suspend fun trendingSearchTerms(
        apikey: String,
        lang: String? = "en",
        countryCode: String? = "US",
        limit: Int? = 20,
    ): SPKeywordListResponse

    suspend fun recentSearch(
        apikey: String,
        userId: String,
    ): SPKeywordListResponse
}
