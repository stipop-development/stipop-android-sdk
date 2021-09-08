package io.stipop.refactor.data.services

import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse
import io.stipop.refactor.domain.services.MyStickersService
import retrofit2.http.*

interface MyStickersRestService : MyStickersService {
    @GET("mysticker/{userId}")
    override suspend fun myStickerPacks(
        @Header("apikey")
        apikey: String,

        @Path("userId")
        userId: String,

        @Query("limit")
        limit: Int?,

        @Query("pageNumber")
        pageNumber: Int?
    ): SPPackageListResponse

    @PUT("mysticker/hide/{userId}/{packageId}")
    override suspend fun hideRecoverMyPack(
        @Header("apikey")
        apikey: String,

        @Path("userId")
        userId: String,

        @Path("packageId")
        packId: Int
    ): SPVoidResponse

    @GET("mysticker/hide/{userId}")
    override suspend fun hiddenStickerPacks(
        @Header("apikey")
        apikey: String,

        @Path("userId")
        userId: String,

        @Query("limit")
        limit: Int?,

        @Query("pageNumber")
        pageNumber: Int?
    ): SPPackageListResponse

    @PUT("mysticker/order/{userId}")
    override suspend fun myStickerOrder(
        @Header("apikey")
        apikey: String,

        @Path("userId")
        userId: String,

        @Query("currentOrder")
        currentOrder: Int,

        @Query("newOrder")
        newOrder: Int
    ): SPVoidResponse
}
