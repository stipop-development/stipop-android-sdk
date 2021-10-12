package io.stipop.data

import android.util.Log
import io.stipop.Config
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage

class AllStickerRepository(private val apiService: StipopApi): BaseRepository() {

    suspend fun getStickerPackages(page: Int, keyword: String?, onSuccess: (data: Any) -> Unit) {
        val result = safeCall(call = {
            apiService.getTrendingStickerPackages(
                userId = Stipop.userId,
                lang = Stipop.lang,
                countryCode = Stipop.countryCode,
                pageNumber =  page,
                limit = Constants.ApiParams.SizePerPage,
                query = keyword
            )
        })
        if(result.body.packageList.isNotEmpty()){
            onSuccess(result.body.packageList)
        }
    }

    suspend fun postDownloadStickers(
        stickerPackage: StickerPackage,
        onSuccess: (data: Any) -> Unit
    ){
        var price: Double?=null
        if (Config.allowPremium == "Y") {
            price = if (stickerPackage.packageAnimated == "Y") {
                Config.gifPrice
            }else{
                Config.pngPrice
            }
        }
        val result = safeCall(call = {
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
        })
        if(result.header.isSuccess()){
            onSuccess(stickerPackage)
        }
    }
}