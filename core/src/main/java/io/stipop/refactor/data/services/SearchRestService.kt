package io.stipop.refactor.data.services

import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPStickerListResponse
import io.stipop.refactor.domain.services.SearchService
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchRestService : SearchService {

    @GET("search")
    override suspend fun stickerSearch(
        @Header("apikey")
        apikey: String,

        @Query("q")
        q: String,

        @Query("userId")
        userId: String,

        @Query("lang")
        lang: String?,

        @Query("countryCode")
        countryCode: String?,

        @Query("limit")
        limit: Int?,

        @Query("pageNumber")
        pageNumber: Int?
    ): SPStickerListResponse

    @GET("search/keyword")
    override suspend fun trendingSearchTerms(
        @Header("apikey")
        apikey: String,

        @Query("userId")
        userId: String,

        @Query("lang")
        lang: String?,

        @Query("countryCode")
        countryCode: String?,

        @Query("limit")
        limit: Int?
    ): SPKeywordListResponse

    @GET("search/recent")
    override suspend fun recentSearch(
        @Header("apikey")
        apikey: String,

        @Query("userId")
        userId: String
    ): SPKeywordListResponse

}
