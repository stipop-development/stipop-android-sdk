package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.VoidResponse

interface MyStickersRepositoryProtocol {
    suspend fun myStickerPacks(
        apikey: String,
        userId: String,
        limit: Int? = 20,
        pageNumber: Int? = 1
    ): PackageListResponse

    suspend fun hideRecoverMyPack(
        apikey: String,
        userId: String,
        packId: Int,
    ): VoidResponse

    suspend fun hiddenStickerPacks(
        apikey: String,
        userId: String,
        limit: Int? = 20,
        pageNumber: Int? = 1
    ): PackageListResponse

    suspend fun myStickerOrder(
        apikey: String,
        userId: String,
        currentOrder: Int,
        newOrder: Int,
    ): VoidResponse
}
