package io.stipop.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import retrofit2.HttpException
import java.io.IOException

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
            val response = if (wantVisibleSticker) apiService.getMyStickers(
                userId = userId,
                limit = limit,
                pageNumber = pageNumber
            ) else apiService.getMyHiddenStickers(
                userId = userId,
                limit = limit,
                pageNumber = pageNumber
            )
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
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception){
            return LoadResult.Error(exception)
        }
    }

}