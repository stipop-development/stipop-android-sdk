package io.stipop.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import kotlinx.coroutines.flow.Flow

internal class SpvRepository(private val apiService: StipopApi): BaseRepository() {
    fun getMyStickerStream(): Flow<PagingData<StickerPackage>> {
        return Pager(
            config = PagingConfig(
                pageSize = MyStickerRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ), pagingSourceFactory = { MyStickerPagingSource(apiService, true) }).flow
    }
}