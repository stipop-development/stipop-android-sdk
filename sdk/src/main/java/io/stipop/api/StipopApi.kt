package io.stipop.api

import android.os.Build
import com.google.gson.Gson
import io.stipop.BuildConfig
import io.stipop.Config
import io.stipop.Constants
import io.stipop.models.body.InitSdkBody
import io.stipop.models.body.OrderChangeBody
import io.stipop.models.body.StipopMetaHeader
import io.stipop.models.body.UserIdBody
import io.stipop.models.response.MyStickerOrderChangedResponse
import io.stipop.models.response.MyStickerResponse
import io.stipop.models.response.StickerPackageResponse
import io.stipop.models.response.StipopResponse
import okhttp3.Authenticator
import okhttp3.Headers
import okhttp3.OkHttpClient
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

    @GET("package")
    suspend fun getTrendingStickerPackages(
        @Query("userId") userId: String,
        @Query("lang") lang: String,
        @Query("countryCode") countryCode: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("q") query: String? = null
    ): Response<StickerPackageResponse>

    @POST("download/{packageId}")
    suspend fun postDownloadStickers(
        @Path("packageId") packageId: Int,
        @Query("userId") userId: String,
        @Query("isPurchase") isPurchase: String,
        @Query("countryCode") countryCode: String,
        @Query("lang") lang: String,
        @Query("price") price: Double? = null,
        @Query("entrance_point") entrancePoint: String? = null,
        @Query("event_point") eventPoint: String? = null,
    ): Response<StipopResponse>

    @POST("sdk/track/config")
    suspend fun trackConfig(@Body userIdBody: UserIdBody): Response<StipopResponse>

    @POST("sdk/track/view/picker")
    suspend fun trackViewPicker(@Body userIdBody: UserIdBody): Response<StipopResponse>

    @POST("sdk/track/view/search")
    suspend fun trackViewSearch(@Body userIdBody: UserIdBody): Response<StipopResponse>

    @POST("sdk/track/view/store")
    suspend fun trackViewStore(@Body userIdBody: UserIdBody): Response<StipopResponse>

    @POST("sdk/track/view/mysticker")
    suspend fun trackViewMySticker(@Body userIdBody: UserIdBody): Response<StipopResponse>

    @POST("sdk/track/view/package/{entrance_point}")
    suspend fun trackViewPackage(@Body userIdBody: UserIdBody, @Path("entrance_point") entrancePoint: String?=Constants.Point.STORE): Response<StipopResponse>

    @POST("analytics/send/{stickerId}")
    suspend fun trackUsingSticker(@Query("event_point") eventPoint: String? = null): Response<StipopResponse>

    companion object {
        fun create(): StipopApi {
            val loggingInterceptor = HttpLoggingInterceptor().apply { level = Level.BASIC }
            val headers = Headers.Builder()
                .add(Constants.ApiParams.ApiKey, if(BuildConfig.DEBUG) Constants.Value.SANDBOX_APIKEY else Config.apikey)
                .add(Constants.ApiParams.SMetadata, Gson().toJson(StipopMetaHeader(platform = Constants.Value.PLATFORM, sdk_version = BuildConfig.SDK_VERSION_NAME, os_version = Build.VERSION.SDK_INT.toString())))
                .build()
            val authenticator = Authenticator { _, response ->
                response.request
                    .newBuilder()
                    .headers(headers)
                    .build()
            }
            val client = OkHttpClient.Builder()
                .authenticator(authenticator)
                .addInterceptor(loggingInterceptor)
                .followRedirects(false)
                .followSslRedirects(false)
                .build()
            return Retrofit.Builder()
                .baseUrl(if(BuildConfig.DEBUG) Constants.Value.SANDBOX_URL else Constants.Value.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StipopApi::class.java)
        }
    }
}
