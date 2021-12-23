package io.stipop.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.stipop.Config
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.response.CurationPackageResponse
import io.stipop.models.response.KeywordListResponse
import io.stipop.models.response.StickerPackagesResponse
import io.stipop.models.response.StipopResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

internal class PkgRepository(private val apiService: StipopApi) : BaseRepository() {

    fun getSearchingPackStream(query: String? = null): Flow<PagingData<StickerPackage>> {
        return Pager(
            config = PagingConfig(
                pageSize = MyStickerRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingPackageSource(
                    apiService,
                    query = query,
                    newOrder = false
                )
            }).flow
    }

    fun getNewPackStream(): Flow<PagingData<StickerPackage>> {
        return Pager(
            config = PagingConfig(
                pageSize = MyStickerRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ), pagingSourceFactory = { PagingPackageSource(apiService, newOrder = true) }).flow
    }

    suspend fun getCurationPackagesAsFlow(type: String): Flow<CurationPackageResponse?> {
        return safeCallAsFlow(call = {
            apiService.getCurationPackages(
                curationType = type,
                userId = Stipop.userId,
                lang = Stipop.lang,
                countryCode = Stipop.countryCode,
            )
        })
    }

    @FlowPreview
    suspend fun getPackagesAsFlow(page: Int): Flow<StickerPackagesResponse?> {
        return safeCallAsFlow(call = {
            apiService.getTrendingStickerPackages(
                userId = Stipop.userId,
                lang = Stipop.lang,
                countryCode = Stipop.countryCode,
                pageNumber = page,
                limit = Constants.ApiParams.SizePerPage,
                query = null
            )
        })
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

    suspend fun postDownloadStickers(
        stickerPackage: StickerPackage,
        onSuccess: (data: StipopResponse?) -> Unit
    ) {
        var price: Double? = null
        if (Config.allowPremium == "Y") {
            price = if (stickerPackage.packageAnimated == "Y") {
                Config.gifPrice
            } else {
                Config.pngPrice
            }
        }
        safeCall(call = {
            apiService.postDownloadStickers(
                packageId = stickerPackage.packageId,
                isPurchase = Config.allowPremium,
                userId = Stipop.userId,
                lang = Stipop.lang,
                countryCode = Stipop.countryCode,
                price = price,
                entrancePoint = Constants.Point.STORE,
                eventPoint = Constants.Point.STORE
            )
        }, onCompletable = {
            onSuccess(it)
        })
    }
}