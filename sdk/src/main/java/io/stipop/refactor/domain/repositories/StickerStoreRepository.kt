package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.PackageResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse

interface StickerStoreRepository {
    val searchPackageList: Observable<List<SPPackage>>
    val allPackageList: Observable<List<SPPackage>>

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

    fun onLoadAllPackageList(
        apikey: String,
        q: String,
        userId: String,
        lang: String? = null,
        countryCode: String? = null,
        premium: String? = null,
        limit: Int? = null,
        pageNumber: Int? = null,
        animated: String? = null
    )

    fun onLoadSearchPackageList(
        apikey: String,
        userId: String,
        keyword: String,
        lastIndex: Int?,
        lang: String? = null,
        countryCode: String? = null,
        premium: String? = null,
        limit: Int? = null,
        pageNumber: Int? = null,
        animated: String? = null
    )

    fun onDownloadPackage(
        apikey: String,
        userId: String,
        pack: SPPackage,
        isPurchase: String = "N",
        lang: String? = null,
        countryCode: String? = null,
        price: String? = null
    )
}
