package io.stipop.refactor.data.services

import io.stipop.refactor.domain.repositories.MyStickersRepositoryProtocol
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.VoidResponse
import retrofit2.http.*

interface MyStickersService : MyStickersRepositoryProtocol {
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
    ): PackageListResponse

    @PUT("mysticker/hide/{userId}/{packageId}")
    override suspend fun hideRecoverMyPack(
        @Header("apikey")
        apikey: String,

        @Path("userId")
        userId: String,

        @Path("packageId")
        packId: Int
    ): VoidResponse

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
    ): PackageListResponse

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
    ): VoidResponse
}
