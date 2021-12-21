package io.stipop.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.Sticker
import io.stipop.models.response.KeywordListResponse
import kotlinx.coroutines.flow.Flow

internal class SearchingRepository(private val apiService: StipopApi) : BaseRepository() {

    companion object {
        const val NETWORK_PAGE_SIZE = 24
    }

    fun getStickersStream(query: String? = null): Flow<PagingData<Sticker>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { PagingStickerSource(apiService, query) }).flow
    }

    suspend fun getRecommendQueryAsFlow(): Flow<KeywordListResponse?> {
        return safeCallAsFlow(call = {
            apiService.getRecommendedKeywords(
                userId = Stipop.userId,
                lang = Stipop.lang,
                countryCode = Stipop.countryCode,
            )
        })
    }
}