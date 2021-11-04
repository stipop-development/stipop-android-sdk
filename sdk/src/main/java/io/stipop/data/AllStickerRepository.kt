package io.stipop.data

import io.stipop.Config
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.response.StipopResponse

internal class AllStickerRepository(private val apiService: StipopApi) : BaseRepository() {

    suspend fun getStickerPackages(page: Int, keyword: String?, onSuccess: (data: Any) -> Unit) {
        safeCall(call = {
            apiService.getTrendingStickerPackages(
                userId = Stipop.userId,
                lang = Stipop.lang,
                countryCode = Stipop.countryCode,
                pageNumber = page,
                limit = Constants.ApiParams.SizePerPage,
                query = keyword
            )
        }, onCompletable = { response ->
            response?.let{
                if(it.body.packageList.isNotEmpty()){
                    onSuccess(it.body.packageList)
                }
            }
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