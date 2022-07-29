package io.stipop.sample

import android.util.Log
import androidx.multidex.MultiDexApplication
import io.stipop.Stipop
import io.stipop.event.SAuthDelegate
import io.stipop.models.enum.StipopApiEnum
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

    /* If you use SAuth, implement this function and refresh accessToken when authorization error occured. */
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