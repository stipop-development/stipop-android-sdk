package io.stipop.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.Sticker
import io.stipop.models.enum.StipopApiEnum
import io.stipop.models.response.StickersResponse
import retrofit2.HttpException

internal class PagingStickerSource(private val apiService: StipopApi, private val query: String? = null) : PagingSource<Int, Sticker>() {

    private val STARTING_PAGE_INDEX = 1
    private val ITEM_PER_PAGE = 12
    private var currentQuery: String? = null

    override fun getRefreshKey(state: PagingState<Int, Sticker>): Int? {
        if (query != currentQuery) {
            currentQuery = query
            return null
        }
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Sticker> {
        val pageNumber = params.key ?: STARTING_PAGE_INDEX
        val userId = Stipop.userId
        var response: StickersResponse? = null
        try {
            response = apiService.getStickers(
                userId = userId,
                limit = ITEM_PER_PAGE,
                pageNumber = pageNumber,
                countryCode = Stipop.countryCode,
                lang = Stipop.lang,
                query = query
            )
            val stickerPackages = response.body?.stickerList ?: emptyList()
            val nextKey = if (stickerPackages.isNullOrEmpty()) {
                null
            } else {
                pageNumber + 1
            }

            return LoadResult.Page(
                data = stickerPackages,
                prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1,
                nextKey = nextKey
            )
        } catch(exception: HttpException){
            when(exception.code()){
                401 -> {
                    Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_STICKERS, exception)
                }
            }
            return LoadResult.Error(exception)
        } catch(exception: Exception){
            Stipop.trackError(exception)
            return LoadResult.Error(exception)
        }
    }
}