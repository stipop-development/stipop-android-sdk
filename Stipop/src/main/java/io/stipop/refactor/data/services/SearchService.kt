package io.stipop.refactor.data.services

import io.stipop.refactor.domain.entities.KeywordListResponse
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.repositories.SearchRepositoryProtocol
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchService : SearchRepositoryProtocol {

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
    ): PackageListResponse

    @GET("search/keyword")
    override suspend fun trendingSearchTerms(
        @Header("apikey")
        apikey: String,

        @Query("lang")
        lang: String?,

        @Query("countryCode")
        countryCode: String?,

        @Query("limit")
        limit: Int?
    ): KeywordListResponse

    @GET("search/recent")
    override suspend fun recentSearch(
        @Header("apikey")
        apikey: String,

        @Query("userId")
        userId: String
    ): KeywordListResponse

}
