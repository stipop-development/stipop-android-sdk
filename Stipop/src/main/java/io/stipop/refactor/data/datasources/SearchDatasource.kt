package io.stipop.refactor.data.datasources

import io.stipop.refactor.data.services.SearchService
import io.stipop.refactor.domain.entities.KeywordListResponse
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.repositories.SearchRepositoryProtocol
import javax.inject.Inject

class SearchDatasource @Inject constructor(
    private val searchService: SearchService
) : SearchRepositoryProtocol {
    override suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
        pageNumber: Int?
    ): PackageListResponse {
        return searchService.stickerSearch(apikey, q, userId, lang, countryCode, limit, pageNumber)
    }

    override suspend fun trendingSearchTerms(
        apikey: String,
        lang: String?,
        countryCode: String?,
        limit: Int?
    ): KeywordListResponse {
        return searchService.trendingSearchTerms(apikey, lang, countryCode, limit)
    }

    override suspend fun recentSearch(apikey: String, userId: String): KeywordListResponse {
        return searchService.recentSearch(apikey, userId)
    }
}
