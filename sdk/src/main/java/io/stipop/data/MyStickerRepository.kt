package io.stipop.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.body.OrderChangeBody
import io.stipop.models.StickerPackage
import io.stipop.models.response.MyStickerOrderResponse
import io.stipop.models.response.StipopResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class MyStickerRepository(private val apiService: StipopApi) {

    private val packageOrderChangedResult = MutableSharedFlow<MyStickerOrderResponse>()
    val packageVisibilityUpdateResult = MutableSharedFlow<Triple<StipopResponse, Int, Int>>()

    fun getMyStickerStream(wantVisibleSticker: Boolean): Flow<PagingData<StickerPackage>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ), pagingSourceFactory = { MyStickerPagingSource(apiService, wantVisibleSticker) }).flow
    }

    suspend fun request(fromStickerPackage: StickerPackage, toStickerPackage: StickerPackage) {
        val apiKey = Config.apikey
        val userId = Stipop.userId
        val fromOrder = fromStickerPackage.order
        val toOrder = toStickerPackage.order
        val response = apiService.putMyStickerOrders(apiKey, userId, OrderChangeBody(fromOrder, toOrder))
        packageOrderChangedResult.emit(response)
    }

    suspend fun updatePackageVisibility(packageId: Int, position: Int) {
        val apiKey = Config.apikey
        val userId = Stipop.userId
        val response = apiService.putMyStickerVisibility(apiKey, userId, packageId)
        packageVisibilityUpdateResult.emit(Triple(response, packageId, position))
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}
