package io.stipop.refactor.domain.services

import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse

interface SearchServiceProtocol {
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
