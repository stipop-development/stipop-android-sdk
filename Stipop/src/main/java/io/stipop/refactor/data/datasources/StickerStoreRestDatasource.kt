package io.stipop.refactor.data.datasources

import io.stipop.refactor.domain.datasources.StickerStoreDatasource
import io.stipop.refactor.domain.entities.PackageResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse
import io.stipop.refactor.domain.services.StickerStoreServiceProtocol
import javax.inject.Inject

class StickerStoreRestDatasource @Inject constructor(
    private val service: StickerStoreServiceProtocol
) : StickerStoreDatasource {
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
    ): SPPackageListResponse {
        return service.trendingStickerPacks(
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

    override suspend fun stickerPackInfo(apikey: String, packId: Int, userId: String): PackageResponse {
        return service.stickerPackInfo(apikey, packId, userId)
    }

    override suspend fun downloadPurchaseSticker(
        apikey: String,
        packId: Int,
        userId: String,
        isPurchase: String,
        lang: String?,
        countryCode: String?,
        price: String?
    ): SPVoidResponse {
        return service.downloadPurchaseSticker(apikey, packId, userId, isPurchase, lang, countryCode, price)
    }

}
