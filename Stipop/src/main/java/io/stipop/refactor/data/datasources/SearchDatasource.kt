package io.stipop.refactor.data.datasources

import io.stipop.refactor.domain.datasources.SearchDatasourceProtocol
import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.services.SearchServiceProtocol
import javax.inject.Inject

class SearchDatasource @Inject constructor(
    private val service: SearchServiceProtocol
) : SearchDatasourceProtocol {
    override suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
        pageNumber: Int?
    ): SPPackageListResponse {
        return service.stickerSearch(apikey, q, userId, lang, countryCode, limit, pageNumber)
    }

    override suspend fun trendingSearchTerms(
        apikey: String,
        lang: String?,
        countryCode: String?,
        limit: Int?
    ): SPKeywordListResponse {
        return service.trendingSearchTerms(apikey, lang, countryCode, limit)
    }

    override suspend fun recentSearch(apikey: String, userId: String): SPKeywordListResponse {
        return service.recentSearch(apikey, userId)
    }
}
