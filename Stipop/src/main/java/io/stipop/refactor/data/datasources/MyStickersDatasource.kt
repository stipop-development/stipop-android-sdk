package io.stipop.refactor.data.datasources

import io.stipop.refactor.domain.datasources.MyStickersDatasourceProtocol
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse
import io.stipop.refactor.domain.services.MyStickersServiceProtocol
import javax.inject.Inject

class MyStickersDatasource @Inject constructor(
    private val myStickersService: MyStickersServiceProtocol
) : MyStickersDatasourceProtocol {
    override suspend fun myStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): SPPackageListResponse {
        return myStickersService.myStickerPacks(apikey, userId, limit, pageNumber)
    }

    override suspend fun hideRecoverMyPack(apikey: String, userId: String, packId: Int): SPVoidResponse {
        return myStickersService.hideRecoverMyPack(apikey, userId, packId)
    }

    override suspend fun hiddenStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): SPPackageListResponse {
        return myStickersService.hiddenStickerPacks(apikey, userId, limit, pageNumber)
    }

    override suspend fun myStickerOrder(
        apikey: String,
        userId: String,
        currentOrder: Int,
        newOrder: Int
    ): SPVoidResponse {
        return myStickersService.myStickerOrder(apikey, userId, currentOrder, newOrder)
    }
}
