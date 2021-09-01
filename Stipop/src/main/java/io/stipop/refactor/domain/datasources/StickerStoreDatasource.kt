package io.stipop.refactor.domain.datasources

import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.PackageResponse
import io.stipop.refactor.domain.entities.SPVoidResponse

interface StickerStoreDatasource {
    suspend fun trendingStickerPacks(
        apikey: String,
        q: String,
        userId: String,
        lang: String? = "en",
        countryCode: String? = "US",
        premium: String?,
        limit: Int? = 20,
        pageNumber: Int? = 1,
        animated: String?
    ): SPPackageListResponse

    suspend fun stickerPackInfo(
        apikey: String,
        packId: Int,
        userId: String,
    ): PackageResponse

    suspend fun downloadPurchaseSticker(
        apikey: String,
        packId: Int,
        userId: String,
        isPurchase: String,
        lang: String? = "en",
        countryCode: String? = "US",
        price: String?,
    ): SPVoidResponse
}
