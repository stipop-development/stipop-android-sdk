package io.stipop.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import io.stipop.models.body.FavoriteBody
import io.stipop.models.body.OrderChangeBody
import io.stipop.models.StipopApiEnum
import io.stipop.models.response.FavoriteListResponse
import io.stipop.models.response.StickerListResponse
import io.stipop.models.response.StickerPackageResponse
import io.stipop.models.response.StipopResponse
import io.stipop.s_auth.GetMyStickerEnum
import io.stipop.s_auth.PutMyStickersOrdersEnum
import io.stipop.s_auth.SAuthManager
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

internal class SpvRepository() : BaseRepository() {

    fun getMyStickerStream(): Flow<PagingData<StickerPackage>> {
        SAuthManager.setGetMyStickersData(GetMyStickerEnum.STICKER_PICKER_VIEW_CLASS)
        return Pager(
            config = PagingConfig(
                pageSize = MyStickerRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ), pagingSourceFactory = { PagingMyPackSource(true) }).flow
    }

    suspend fun requestChangePackOrder(fromStickerPackage: StickerPackage, toStickerPackage: StickerPackage) {
        val userId = Stipop.userId
        val fromOrder = fromStickerPackage.order
        val toOrder = toStickerPackage.order
        try {
            StipopApi.create().putMyStickerOrders(userId = userId, orderChangeBody = OrderChangeBody(fromOrder, toOrder))
        } catch (exception: HttpException) {
            if (exception.code() == 401) {
                SAuthManager.setPutMyStickersOrdersData(PutMyStickersOrdersEnum.STICKER_PICKER_VIEW_MODEL, fromStickerPackage, toStickerPackage)
                Stipop.sAuthDelegate?.httpException(StipopApiEnum.PUT_MY_STICKERS_ORDERS, exception)
            }
        } catch (exception: Exception){
            Stipop.trackError(exception)
        }
    }

    suspend fun getStickerPackage(
        packageId: Int,
        onSuccess: (data: StickerPackageResponse) -> Unit
    ) {
        safeCall(
            call = { StipopApi.create().getStickerPackage(packageId = packageId, userId = Stipop.userId) },
            onCompletable = {
                it?.let(onSuccess)
            })
    }

    suspend fun putFavorite(spSticker: SPSticker, onSuccess: (data: StipopResponse) -> Unit) {
        safeCall(
            call = {
                StipopApi.create().putMyStickerFavorite(
                    userId = Stipop.userId,
                    favoriteBody = FavoriteBody(spSticker.stickerId)
                )
            },
            onCompletable = {
                it?.let(onSuccess)
            })
    }

    suspend fun getFavorites(onSuccess: (data: FavoriteListResponse) -> Unit) {
        safeCall(
            call = { StipopApi.create().getFavoriteStickers(userId = Stipop.userId, pageNumber = 1, limit = 24) },
            onCompletable = {
                it?.let(onSuccess)
            }
        )
    }

    suspend fun getRecentlySentStickers(onSuccess: (data: StickerListResponse) -> Unit) {
        safeCall(
            call = { StipopApi.create().getRecentlySentStickers(userId = Stipop.userId, userIdQuery = Stipop.userId, pageNumber = 1, limit = 24) },
            onCompletable = {
                it?.let(onSuccess)
            }
        )
    }
}