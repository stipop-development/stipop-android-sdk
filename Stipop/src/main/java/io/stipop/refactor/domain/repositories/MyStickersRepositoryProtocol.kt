package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse

interface MyStickersRepositoryProtocol {
    suspend fun myStickerPacks(
        apikey: String,
        userId: String,
        limit: Int? = 20,
        pageNumber: Int? = 1
    ): SPPackageListResponse

    suspend fun hideRecoverMyPack(
        apikey: String,
        userId: String,
        packId: Int,
    ): SPVoidResponse

    suspend fun hiddenStickerPacks(
        apikey: String,
        userId: String,
        limit: Int? = 20,
        pageNumber: Int? = 1
    ): SPPackageListResponse

    suspend fun myStickerOrder(
        apikey: String,
        userId: String,
        currentOrder: Int,
        newOrder: Int,
    ): SPVoidResponse
}
