package io.stipop.data

import io.stipop.api.StipopApi
import io.stipop.models.body.InitSdkBody
import io.stipop.models.body.UserIdBody
import io.stipop.models.response.StipopResponse

internal class ConfigRepository(private val apiService: StipopApi) : BaseRepository() {

    var currentUserId: String? = null

    suspend fun postInitSdk(initSdkBody: InitSdkBody, onSuccess: (data: Any) -> Unit) {
        initSdkBody.userId?.let {
            if (currentUserId == it) {
                onSuccess(Unit)
            } else {
                currentUserId = it
                safeCall(call = { apiService.initSdk(initSdkBody) }, onCompletable = {
                    onSuccess(Unit)
                })
            }
        } ?: kotlin.run {
            onSuccess(Unit)
        }
    }

    suspend fun postConfigSdk(onSuccess: (data: Any?) -> Unit) {
        safeCall(call = { apiService.trackConfig(UserIdBody()) }, onCompletable = { onSuccess(it) })
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
        safeCall(call = {
            apiService.trackUsingSticker(
                stickerId = stickerId,
                userId = userId,
                query = query,
                countryCode = countryCode,
                lang = lang,
                eventPoint = eventPoint
            )
        }, onCompletable = {
            it?.let(onSuccess)
        })
    }
}