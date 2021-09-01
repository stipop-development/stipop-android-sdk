package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse

interface MyStickersRepository {
    val hiddenPackageList: Observable<List<SPPackage>>
    val activePackageList: Observable<List<SPPackage>>

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

    fun onLoadActivePackageList(
        apikey: String,
        userId: String,
        limit: Int? = null,
        pageNumber: Int? = null,
    )
    fun onLoadHiddenPackageList(
        apikey: String,
        userId: String,
        limit: Int? = null,
        pageNumber: Int? = null,
    )

    fun onActivePackage(apikey: String, userId: String, value: SPPackage)
    fun onHiddenPackage(apikey: String, userId: String, value: SPPackage)
}
