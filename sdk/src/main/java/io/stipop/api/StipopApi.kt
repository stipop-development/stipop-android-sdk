package io.stipop.api

import io.stipop.Config
import io.stipop.Constants
import io.stipop.models.body.InitSdkBody
import io.stipop.models.body.OrderChangeBody
import io.stipop.models.response.MyStickerOrderChangedResponse
import io.stipop.models.response.MyStickerResponse
import io.stipop.models.response.StipopResponse
import okhttp3.*
import okhttp3.Headers
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface StipopApi {

    @POST("init")
    suspend fun initSdk(
        @Body initSdkBody: InitSdkBody
    ): Response<StipopResponse>

    @GET("mysticker/{userId}")
    suspend fun getMyStickers(
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int
    ): MyStickerResponse

    @GET("mysticker/hide/{userId}")
    suspend fun getMyHiddenStickers(
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int
    ): MyStickerResponse

    @PUT("mysticker/order/{userId}")
    suspend fun putMyStickerOrders(
        @Path("userId") userId: String,
        @Body orderChangeBody: OrderChangeBody
    ): MyStickerOrderChangedResponse

    @PUT("mysticker/hide/{userId}/{packageId}")
    suspend fun putMyStickerVisibility(
        @Path("userId") userId: String,
        @Path("packageId") packageId: Int
    ): StipopResponse

    companion object {
        private const val BASE_URL = "https://messenger.stipop.io/v1/"
        fun create(): StipopApi {
            val loggingInterceptor = HttpLoggingInterceptor().apply { level = Level.BASIC }
            val requestInterceptor = Interceptor { chain ->
                val original = chain.request()
                val modifiedUrl = chain.request().url.newBuilder()
                    .addQueryParameter(Constants.ApiParams.Platform, "android-sdk").build()
                chain.proceed(original.newBuilder().url(modifiedUrl).build())
            }
            val headers = Headers.Builder().add(Constants.ApiParams.ApiKey, Config.apikey).build()
            val authenticator = Authenticator { _, response ->
                response.request
                    .newBuilder()
                    .headers(headers)
                    .build()
            }
            val client = OkHttpClient.Builder()
                .authenticator(authenticator)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(requestInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StipopApi::class.java)
        }
    }
}
