package io.stipop.refactor.data.repositories

import dagger.Module
import io.stipop.refactor.data.datasources.StickerStoreDatasource
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.PackageResponse
import io.stipop.refactor.domain.entities.VoidResponse
import io.stipop.refactor.domain.repositories.StickerStoreRepositoryProtocol
import javax.inject.Inject

@Module
class StickerStoreRepository  @Inject constructor(
    private val remoteDatasource: StickerStoreDatasource,
): StickerStoreRepositoryProtocol {
    val packageList: ArrayList<SPPackage> = arrayListOf()

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
        return remoteDatasource.trendingStickerPacks(
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
        return remoteDatasource.stickerPackInfo(apikey, packId, userId)
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
        return remoteDatasource.downloadPurchaseSticker(apikey, packId, userId, isPurchase, lang, countryCode, price)
    }
}
