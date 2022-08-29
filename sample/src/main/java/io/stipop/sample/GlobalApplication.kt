package io.stipop.sample

import android.content.res.Configuration
import android.util.Log
import androidx.multidex.MultiDexApplication
import io.stipop.Stipop
import io.stipop.event.SAuthDelegate
import io.stipop.models.StipopApiEnum
import io.stipop.s_auth.SAuthManager
import io.stipop.sample.stipop_auth.SAuthRepository
import kotlinx.coroutines.*
import retrofit2.HttpException

class GlobalApplication : MultiDexApplication(), SAuthDelegate {

    override fun onCreate() {
        super.onCreate()
        Stipop.configure(this,
            sAuthDelegate = this,   // If you do not use SAuth, type null
            callback = {
                Log.d(this.javaClass.name, "Use callback if you need.")
            })
    }

    /**
     * httpException (From SAuthDelegate)
     * :If HttpException occurs in Stipop, occurred HttpException will be received in here.
     *
     * @process 1: Issue new AccessToken (If other exception is issuing an AccessToken, please wait until finishing and request using this AccessToken).
     *          2: Set new AccessToken to Stipop. (Using Stipop.setAccessToken method)
     *          3: Rerequest to the API where error occurred. (Using SAuthManager.reRequest method)
     * @param api: Where HttpException occurred.
     * @param exception: HttpException occurred.
     */
    override fun httpException(api: StipopApiEnum, exception: HttpException) {
        when(exception.code()){
            401 -> {
                CoroutineScope(Job() + Dispatchers.IO).launch {
                    while(SAuthRepository.getIsSAuthWorking()){
                        delay(50)
                    }
                    val accessToken = SAuthRepository.getAccessTokenIfOverExpiryTime(userId = Stipop.userId)
                    Stipop.setAccessToken(accessToken = accessToken)
                    SAuthManager.reRequest(api)
                }
            }
        }
    }
}