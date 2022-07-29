package io.stipop.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.enum.StipopApiEnum
import io.stipop.models.response.MyStickerResponse
import retrofit2.HttpException

internal class PagingMyPackSource(private val apiService: StipopApi, private val wantVisibleSticker: Boolean) : PagingSource<Int, StickerPackage>() {

    private val STARTING_PAGE_INDEX = 1
    private var currentVisibleSetting = true

    override fun getRefreshKey(state: PagingState<Int, StickerPackage>): Int? {
        if (wantVisibleSticker != currentVisibleSetting) {
            currentVisibleSetting = wantVisibleSticker
            return null
        }
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StickerPackage> {
        val pageNumber = params.key ?: STARTING_PAGE_INDEX
        val userId = Stipop.userId
        val limit = 20
        return try {
            var response: MyStickerResponse? = null
            if (wantVisibleSticker) {
                try {
                    response = apiService.getMyStickers(
                        userId = userId,
                        userIdQuery = userId,
                        limit = limit,
                        pageNumber = pageNumber
                    )
                } catch(exception: HttpException){
                    when(exception.code()){
                        401 -> {
                            Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_MY_STICKERS, exception)
                            return LoadResult.Error(exception)
                        }
                    }
                }
            } else {
                try {
                    response = apiService.getMyHiddenStickers(
                        userId = userId,
                        limit = limit,
                        pageNumber = pageNumber
                    )
                } catch(exception: HttpException){
                    when(exception.code()){
                        401 -> {
                            Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_MY_HIDDEN_STICKERS, exception)
                            return LoadResult.Error(exception)
                        }
                    }
                }
            }
            val myStickers = response.body.packageList
            val nextKey = if (myStickers.isNullOrEmpty()) {
                null
            } else {
                pageNumber + 1
            }
            LoadResult.Page(
                data = myStickers,
                prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1,
                nextKey = nextKey
            )
        } catch(exception: Exception){
            Stipop.trackError(exception)
            return LoadResult.Error(exception)
        }
    }
}