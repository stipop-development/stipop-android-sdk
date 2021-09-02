package io.stipop.refactor.data.services

import io.stipop.refactor.domain.entities.SPStickerListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse
import io.stipop.refactor.domain.services.StickerSendService
import retrofit2.http.*

interface StickerSendRestService : StickerSendService {

    @POST("analytics/send/{stickerId}")
    override suspend fun registerStickerSend(
        @Header("apikey")
        apikey: String,

        @Path("stickerId")
        stickerId: Int,

        @Query("userId")
        userId: String,

        @Query("lang")
        lang: String?,

        @Query("countryCode")
        countryCode: String?,

        @Query("q")
        q: String?
    ): SPVoidResponse

    @GET("package/send/{userId}")
    override suspend fun recentlySentStickers(
        @Header("apikey")
        apikey: String,

        @Path("userId")
        userId: String,

        @Query("limit")
        limit: Int?,

        @Query("pageNumber")
        pageNumber: Int?,
    ): SPStickerListResponse

    @GET("package/frequently/{userId}")
    override suspend fun frequentlySentStickers(
        @Header("apikey")
        apikey: String,

        @Path("userId")
        userId: String,

        @Query("limit")
        limit: Int?,

        @Query("pageNumber")
        pageNumber: Int?,
    ): SPStickerListResponse
}
