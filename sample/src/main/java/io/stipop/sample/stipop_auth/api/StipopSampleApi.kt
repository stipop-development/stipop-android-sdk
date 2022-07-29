package io.stipop.sample.stipop_auth.api

import androidx.annotation.Keep
import io.stipop.sample.stipop_auth.model.GetAccessTokenAPIBody
import io.stipop.sample.stipop_auth.model.GetNewAccessTokenResponse
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@Keep
internal interface StipopSampleApi {

    @POST("access")
    suspend fun getAccessToken(
        @Body getAccessTokenAPIBody: GetAccessTokenAPIBody
    ): GetNewAccessTokenResponse

    companion object {

        private val BASE_URL = "https://messenger.stipop.io/v1/"
        private val API_KEY_VALUE = "YOUR_API_KEY_VALUE"

        private val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        private val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(Interceptor {
                it.proceed(it.request().newBuilder().headers(getHeaders()).build())
            })
            .addNetworkInterceptor {
                it.proceed(it.request())
            }
            .build()

        fun create(): StipopSampleApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StipopSampleApi::class.java)
        }
        private fun getHeaders(): Headers {
            return Headers.Builder()
                .add("api_key", API_KEY_VALUE)
                .build()
        }
    }
}