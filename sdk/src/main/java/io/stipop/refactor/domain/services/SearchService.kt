package io.stipop.refactor.domain.services

import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPStickerListResponse

interface SearchService {
    suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
        pageNumber: Int?
    ): SPStickerListResponse

    suspend fun trendingSearchTerms(
        apikey: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
    ): SPKeywordListResponse

    suspend fun recentSearch(
        apikey: String,
        userId: String,
    ): SPKeywordListResponse
}
