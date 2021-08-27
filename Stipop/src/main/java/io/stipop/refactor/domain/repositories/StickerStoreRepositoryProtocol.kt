package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.PackageResponse
import io.stipop.refactor.domain.entities.VoidResponse

interface StickerStoreRepositoryProtocol {
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
    ): PackageListResponse

    suspend fun stickerPackInfo(
        apikey: String,
        packId: String,
        userId: String,
    ): PackageResponse

    suspend fun downloadPurchaseSticker(
        apikey: String,
        packId: String,
        userId: String,
        isPurchase: String,
        lang: String? = "en",
        countryCode: String? = "US",
        price: String?,
    ): VoidResponse
}
