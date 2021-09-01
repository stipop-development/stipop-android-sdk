package io.stipop.refactor.domain.datasources

import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.services.SearchServiceProtocol
import javax.inject.Inject

interface SearchDatasource {
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
