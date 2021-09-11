package io.stipop.refactor.data.datasources

import android.util.Log
import io.stipop.refactor.domain.datasources.SearchDatasource
import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPStickerListResponse
import io.stipop.refactor.domain.services.SearchService
import javax.inject.Inject

class SearchRestDatasource @Inject constructor(
    private val service: SearchService
) : SearchDatasource {
    override suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
        pageNumber: Int?
    ): SPStickerListResponse {
        return service.stickerSearch(apikey, q, userId, lang, countryCode, limit, pageNumber)
    }

    override suspend fun trendingSearchTerms(
        apikey: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?
    ): SPKeywordListResponse {
        return service.trendingSearchTerms(apikey, userId, lang, countryCode, limit)
    }

    override suspend fun recentSearch(apikey: String, userId: String): SPKeywordListResponse {
        return service.recentSearch(apikey, userId)
    }
}
