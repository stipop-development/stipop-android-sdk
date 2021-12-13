package io.stipop.data

import io.stipop.Config
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.response.StickerPackageResponse
import io.stipop.models.response.StipopResponse

internal class StickerDetailRepository(private val apiService: StipopApi) : BaseRepository() {

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