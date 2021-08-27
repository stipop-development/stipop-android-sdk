package io.stipop.refactor.data.datasources

import io.stipop.refactor.data.services.MyStickersService
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.VoidResponse
import io.stipop.refactor.domain.repositories.MyStickersRepositoryProtocol
import javax.inject.Inject

class MyStickersDatasource @Inject constructor(
    private val myStickersService: MyStickersService
) : MyStickersRepositoryProtocol {
    override suspend fun myStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): PackageListResponse {
        return myStickersService.myStickerPacks(apikey, userId, limit, pageNumber)
    }

    override suspend fun hideRecoverMyPack(apikey: String, userId: String, packId: Int): VoidResponse {
        return myStickersService.hideRecoverMyPack(apikey, userId, packId)
    }

    override suspend fun hiddenStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): PackageListResponse {
        return myStickersService.hiddenStickerPacks(apikey, userId, limit, pageNumber)
    }

    override suspend fun myStickerOrder(
        apikey: String,
        userId: String,
        currentOrder: Int,
        newOrder: Int
    ): VoidResponse {
        return myStickersService.myStickerOrder(apikey, userId, currentOrder, newOrder)
    }
}
