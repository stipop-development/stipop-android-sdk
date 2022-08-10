package io.stipop.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.body.OrderChangeBody
import io.stipop.models.StipopApiEnum
import io.stipop.models.response.MyStickerOrderChangedResponse
import io.stipop.models.response.StipopResponse
import io.stipop.s_auth.GetMyStickerEnum
import io.stipop.s_auth.PutMyStickersOrdersEnum
import io.stipop.s_auth.SAuthManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException

internal class MyStickerRepository(private val apiService: StipopApi): BaseRepository() {

    private val packageOrderChangedResult = MutableSharedFlow<MyStickerOrderChangedResponse>()
    val packageVisibilityUpdateResult = MutableSharedFlow<Triple<StipopResponse, Int, Int>>()

    fun getMyStickerStream(wantVisibleSticker: Boolean): Flow<PagingData<StickerPackage>> {
        SAuthManager.setGetMyStickersData(GetMyStickerEnum.STORE_MY_STICKER_FRAGMENT)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ), pagingSourceFactory = { PagingMyPackSource(apiService, wantVisibleSticker) }).flow
    }

    suspend fun requestChangePackOrder(fromStickerPackage: StickerPackage, toStickerPackage: StickerPackage) {
        val userId = Stipop.userId
        val fromOrder = fromStickerPackage.order
        val toOrder = toStickerPackage.order
        var response: MyStickerOrderChangedResponse? = null
        try {
            try {
                response = apiService.putMyStickerOrders(
                    userId = userId,
                    orderChangeBody = OrderChangeBody(fromOrder, toOrder)
                )
                packageOrderChangedResult.emit(response)
            } catch (exception: HttpException) {
                if (exception.code() == 401) {
                    SAuthManager.setPutMyStickersOrdersData(PutMyStickersOrdersEnum.STORE_MY_STICKER_VIEW_MODEL, fromStickerPackage, toStickerPackage)
                    Stipop.sAuthDelegate?.httpException(StipopApiEnum.PUT_MY_STICKERS_ORDERS, exception)
                }
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    suspend fun updatePackageVisibility(packageId: Int, position: Int) {
        val userId = Stipop.userId
        var response: StipopResponse? = null
        try {
            try {
                response = apiService.putMyStickerVisibility(userId = userId, packageId = packageId)
                packageVisibilityUpdateResult.emit(Triple(response, packageId, position))
            } catch (exception: HttpException) {
                if (exception.code() == 401) {
                    SAuthManager.setPutMyStickerVisibilityData(packageId, position)
                    Stipop.sAuthDelegate?.httpException(StipopApiEnum.PUT_MY_STICKER_VISIBILITY, exception)
                }
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}
