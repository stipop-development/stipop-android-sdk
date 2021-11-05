package io.stipop.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import retrofit2.HttpException
import java.io.IOException

internal class StickerPackagePagingSource(private val apiService: StipopApi) :
    PagingSource<Int, StickerPackage>() {

    private val STARTING_PAGE_INDEX = 1

    override fun getRefreshKey(state: PagingState<Int, StickerPackage>): Int? {
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
            val response = apiService.getTrendingStickerPackages(
                userId = userId,
                limit = limit,
                pageNumber = pageNumber,
                countryCode = Stipop.countryCode,
                lang = Stipop.lang
            )
            val stickerPackages = response.body.packageList
            val nextKey = if (stickerPackages.isNullOrEmpty()) {
                null
            } else {
                pageNumber + 1
            }
            LoadResult.Page(
                data = stickerPackages,
                prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

}