package io.stipop.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.response.StickerPackageResponse
import kotlinx.coroutines.flow.Flow

internal class SpvRepository(private val apiService: StipopApi): BaseRepository() {
    fun getMyStickerStream(): Flow<PagingData<StickerPackage>> {
        return Pager(
            config = PagingConfig(
                pageSize = MyStickerRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ), pagingSourceFactory = { PagingMyPackSource(apiService, true) }).flow
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
}