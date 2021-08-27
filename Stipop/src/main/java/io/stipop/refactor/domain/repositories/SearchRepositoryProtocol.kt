package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.KeywordListResponse
import io.stipop.refactor.domain.entities.PackageListResponse

interface SearchRepositoryProtocol {
    suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String? = "en",
        countryCode: String? = "US",
        limit: Int? = 20,
        pageNumber: Int? = 1
    ): PackageListResponse

    suspend fun trendingSearchTerms(
        apikey: String,
        lang: String? = "en",
        countryCode: String? = "US",
        limit: Int? = 20,
    ): KeywordListResponse

    suspend fun recentSearch(
        apikey: String,
        userId: String,
    ): KeywordListResponse
}
