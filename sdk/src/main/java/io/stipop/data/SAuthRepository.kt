package io.stipop.data

import io.stipop.Config
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.body.GetAcceesTokenAPIBody

internal class SAuthRepository : BaseRepository() {

    companion object {
        suspend fun getAccessToken() {
            if(Config.sAuthIsActive) {
                val currentTimeMillis = System.currentTimeMillis()
                val refreshAccessTokenSeconds = if(Config.sAuthExpiryTimeSeconds > 1) (Config.sAuthExpiryTimeSeconds - 1) else Config.sAuthExpiryTimeSeconds
                val expiryTimeMillis = (refreshAccessTokenSeconds * 1000).toLong()
                val userId = Stipop.userId
                val result = StipopApi.create()
                    .getAccessToken(getAccessTokenAPIBody = GetAcceesTokenAPIBody(userId = userId))
                Stipop.sAuthAccessToken = result.body?.accessToken ?: ""
                Stipop.sAuthAccessTokenUserId = userId
                Stipop.sAuthAccessTokenExpiryTimeMillis = (currentTimeMillis + expiryTimeMillis)
            }
        }
    }
}