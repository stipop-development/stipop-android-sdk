package io.stipop.refactor.data.datasources

import io.stipop.refactor.data.services.StickerStoreService
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.PackageResponse
import io.stipop.refactor.domain.entities.VoidResponse
import io.stipop.refactor.domain.repositories.StickerStoreRepositoryProtocol
import javax.inject.Inject

class StickerStoreDatasource @Inject constructor(
    private val stickerStoreService: StickerStoreService
) : StickerStoreRepositoryProtocol {
    override suspend fun trendingStickerPacks(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        premium: String?,
        limit: Int?,
        pageNumber: Int?,
        animated: String?
    ): PackageListResponse {
        return stickerStoreService.trendingStickerPacks(
            apikey,
            q,
            userId,
            lang,
            countryCode,
            premium,
            limit,
            pageNumber,
            animated
        )
    }

    override suspend fun stickerPackInfo(apikey: String, packId: String, userId: String): PackageResponse {
        return stickerStoreService.stickerPackInfo(apikey, packId, userId)
    }

    override suspend fun downloadPurchaseSticker(
        apikey: String,
        packId: String,
        userId: String,
        isPurchase: String,
        lang: String?,
        countryCode: String?,
        price: String?
    ): VoidResponse {
        return stickerStoreService.downloadPurchaseSticker(apikey, packId, userId, isPurchase, lang, countryCode, price)
    }

}
