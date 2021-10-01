package io.stipop.data

import io.stipop.api.StipopApi
import io.stipop.models.body.InitSdkBody

class ConfigRepository(private val apiService: StipopApi) : BaseRepository() {

    suspend fun postInitSdk(initSdkBody: InitSdkBody, onSuccess: (data: Any) -> Unit) {
        onSuccess(safeCall(call = { apiService.initSdk(initSdkBody) }))
    }
}