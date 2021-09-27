package io.stipop.api

import io.stipop.models.body.OrderChangeBody
import io.stipop.models.response.MyStickerOrderResponse
import io.stipop.models.response.MyStickerResponse
import io.stipop.models.response.StipopResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface StipopApi {

    @GET("mysticker/{userId}")
    suspend fun getMyStickers(
        @Header("apiKey") apiKey: String,
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("platform") platform: String = "android-sdk",
    ): MyStickerResponse

    @GET("mysticker/hide/{userId}")
    suspend fun getMyHiddenStickers(
        @Header("apiKey") apiKey: String,
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("platform") platform: String = "android-sdk",
    ): MyStickerResponse

    @PUT("mysticker/order/{userId}")
    suspend fun putMyStickerOrders(
        @Header("apiKey") apiKey: String,
        @Path("userId") userId: String,
        @Body orderChangeBody: OrderChangeBody,
        @Query("platform") platform: String = "android-sdk",
    ): MyStickerOrderResponse

    @PUT("mysticker/hide/{userId}/{packageId}")
    suspend fun putMyStickerVisibility(
        @Header("apiKey") apiKey: String,
        @Path("userId") userId: String,
        @Path("packageId") packageId: Int,
        @Query("platform") platform: String = "android-sdk",
    ): StipopResponse

    companion object {
        private const val BASE_URL = "https://messenger.stipop.io/v1/"
        fun create(): StipopApi {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
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
