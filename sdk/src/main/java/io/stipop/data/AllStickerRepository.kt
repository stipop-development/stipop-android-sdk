package io.stipop.data

import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.api.StipopApi

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

}