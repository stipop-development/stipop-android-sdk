package io.stipop.data

import io.stipop.Config
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.response.StickerPackageResponse
import io.stipop.models.response.StipopResponse

internal class StickerDetailRepository() : BaseRepository() {

    suspend fun getStickerPackage(
        packageId: Int,
        onSuccess: (data: StickerPackageResponse) -> Unit
    ) {
        safeCall(
            call = { StipopApi.create().getStickerPackage(packageId = packageId, userId = Stipop.userId) },
            onCompletable = {
                it?.let(onSuccess)
            })
    }

    suspend fun postDownloadStickers(
        stickerPackage: StickerPackage,
        onSuccess: (data: StipopResponse?) -> Unit
    ) {
        val isPurchaseMode = if (Config.isPackPurchaseMode) {
            "Y"
        } else {
            "N"
        }
        val priceTier = stickerPackage.getPriceTier()
        val priceQueryValue = if (Config.isPackPurchaseMode) {
            priceTier?.price
        } else {
            null
        }

        priceTier?.let {
            safeCall(
                call = {
                    StipopApi.create().postDownloadStickers(
                        packageId = stickerPackage.packageId,
                        isPurchase = isPurchaseMode,
                        userId = Stipop.userId,
                        lang = Stipop.lang,
                        countryCode = Stipop.countryCode,
                        price = priceQueryValue,
                        entrancePoint = Constants.Point.STORE,
                        eventPoint = Constants.Point.STORE
                    )
                }, onCompletable = {
                    onSuccess(it)
                })
        }
    }
}