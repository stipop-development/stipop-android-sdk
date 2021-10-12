package io.stipop.data

import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.body.InitSdkBody
import io.stipop.models.body.UserIdBody
import io.stipop.models.response.StipopResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

class ConfigRepository(private val apiService: StipopApi) : BaseRepository() {

    suspend fun postInitSdk(initSdkBody: InitSdkBody, onSuccess: (data: Any) -> Unit) {
        onSuccess(safeCall(call = { apiService.initSdk(initSdkBody) }))
    }

    suspend fun postTrackUsingSticker(
        stickerId: String,
        userId: String,
        query: String?,
        countryCode: String,
        lang: String,
        eventPoint: String?,
        onSuccess: (data: StipopResponse) -> Unit
    ) {
        onSuccess(safeCall(call = {
            apiService.trackUsingSticker(
                stickerId = stickerId,
                userId = userId,
                query = query,
                countryCode = countryCode,
                lang = lang,
                eventPoint = eventPoint
            )
        }))
    }
}