package io.stipop.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.body.OrderChangeBody
import io.stipop.models.response.MyStickerOrderChangedResponse
import io.stipop.models.response.StipopResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException

internal class MyStickerRepository(private val apiService: StipopApi): BaseRepository() {

    private val packageOrderChangedResult = MutableSharedFlow<MyStickerOrderChangedResponse>()
    val packageVisibilityUpdateResult = MutableSharedFlow<Triple<StipopResponse, Int, Int>>()

    fun getMyStickerStream(wantVisibleSticker: Boolean): Flow<PagingData<StickerPackage>> {
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
                    SAuthRepository.getAccessToken()
                    response = apiService.putMyStickerOrders(
                        userId = userId,
                        orderChangeBody = OrderChangeBody(fromOrder, toOrder)
                    )
                    packageOrderChangedResult.emit(response)
                }
            }
        } catch(exception: Exception){

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
                    SAuthRepository.getAccessToken()
                    response = apiService.putMyStickerVisibility(userId = userId, packageId = packageId)
                    packageVisibilityUpdateResult.emit(Triple(response, packageId, position))
                }
            }
        } catch(exception: Exception){
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}
