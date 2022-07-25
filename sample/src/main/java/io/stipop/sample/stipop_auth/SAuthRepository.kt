package io.stipop.sample.stipop_auth

import com.google.gson.annotations.SerializedName
import io.stipop.sample.stipop_auth.api.StipopSampleApi
import io.stipop.sample.stipop_auth.model.GetAccessTokenAPIBody
import io.stipop.sample.stipop_auth.model.GetNewAccessTokenResponse

class SAuthRepository {

    companion object {

        private var appId: String = "YOUR_APP_ID"
        private var clientId: String = "YOUR_APP_CLIENT_ID"
        private var clientSecret: String = "YOUR_APP_CLIENT_SECRET"
        private var refreshToken: String = "YOUR_APP_REFRESH_TOKEN"
        private var expiryTime: Int = 3600

        private var isSAuthWorking = false

        private var sAuthAccessToken = ""
        private var sAuthAccessTokenUserId = ""
        private var shouldRefreshAccessTokenTimeMillis = 0L

        internal suspend fun getAccessTokenIfOverExpiryTime(userId: String): String {
            setIsSAuthWorking(true)
            val currentTimeMillis = System.currentTimeMillis()
            if (sAuthAccessTokenUserId != userId) {
                return getAccessToken(userId)
            } else if (currentTimeMillis >= shouldRefreshAccessTokenTimeMillis) {
                return getAccessToken(userId)
            } else {
                setIsSAuthWorking(false)
                return sAuthAccessToken
            }
        }

        private suspend fun getAccessToken(userId: String): String {
            val result = StipopSampleApi.create().getAccessToken(
                getAccessTokenAPIBody = GetAccessTokenAPIBody(
                    appId = appId,
                    userId = userId,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    refreshToken = refreshToken,
                    expiryTime = expiryTime
                )
            )
            setSAuthInformation(result, userId)
            setIsSAuthWorking(false)
            return sAuthAccessToken
        }

        private fun setIsSAuthWorking(isSAuthWorking: Boolean){
            this.isSAuthWorking = isSAuthWorking
        }

        fun getIsSAuthWorking(): Boolean{
            return isSAuthWorking
        }

        private fun setSAuthInformation(result: GetNewAccessTokenResponse, userId: String){
            val currentTimeMillis = System.currentTimeMillis()
            val expiryTimeMillis = ((if(expiryTime > 60) (expiryTime - 10) else (expiryTime - 1))* 1000).toLong()
            sAuthAccessToken = result.body?.accessToken ?: ""
            sAuthAccessTokenUserId = userId
            shouldRefreshAccessTokenTimeMillis = (currentTimeMillis + expiryTimeMillis)
        }
    }
}


