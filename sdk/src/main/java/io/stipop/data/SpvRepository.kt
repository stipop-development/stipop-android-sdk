package io.stipop.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Stipop
import io.stipop.StipopUtils
import io.stipop.api.StipopApi
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import io.stipop.models.body.FavoriteBody
import io.stipop.models.body.OrderChangeBody
import io.stipop.models.response.FavoriteListResponse
import io.stipop.models.response.StickerListResponse
import io.stipop.models.response.StickerPackageResponse
import io.stipop.models.response.StipopResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class SpvRepository(private val apiService: StipopApi) : BaseRepository() {

    fun getMyStickerStream(): Flow<PagingData<StickerPackage>> {
        return Pager(
            config = PagingConfig(
                pageSize = MyStickerRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ), pagingSourceFactory = { PagingMyPackSource(apiService, true) }).flow
    }

    suspend fun requestChangePackOrder(fromStickerPackage: StickerPackage, toStickerPackage: StickerPackage) {
        val userId = Stipop.userId
        val fromOrder = fromStickerPackage.order
        val toOrder = toStickerPackage.order
        apiService.putMyStickerOrders(userId, OrderChangeBody(fromOrder, toOrder))
    }

    suspend fun getStickerPackage(
        packageId: Int,
        onSuccess: (data: StickerPackageResponse) -> Unit
    ) {
        safeCall(
            call = { apiService.getStickerPackage(packageId, Stipop.userId) },
            onCompletable = {
                it?.let(onSuccess)
            })
    }

    suspend fun putFavorite(spSticker: SPSticker, onSuccess: (data: StipopResponse) -> Unit) {
        safeCall(
            call = {
                apiService.putMyStickerFavorite(
                    Stipop.userId,
                    FavoriteBody(spSticker.stickerId)
                )
            },
            onCompletable = {
                it?.let(onSuccess)
            })
    }

    suspend fun getFavorites(onSuccess: (data: FavoriteListResponse) -> Unit) {
        safeCall(
            call = { apiService.getFavoriteStickers(Stipop.userId, 1, 24) },
            onCompletable = {
                it?.let(onSuccess)
            }
        )
    }
}