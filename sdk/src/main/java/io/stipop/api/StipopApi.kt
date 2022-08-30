package io.stipop.api

import android.os.Build
import androidx.annotation.Keep
import com.google.gson.Gson
import io.stipop.BuildConfig
import io.stipop.Config
import io.stipop.Constants
import io.stipop.models.body.*
import io.stipop.models.response.*
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit

@Keep
internal interface StipopApi {

    @POST("init")
    suspend fun initSdk(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Body initSdkBody: InitSdkBody
    ): StipopResponse

    @GET("curation/type/{type}")
    suspend fun getCurationPackages(
        @Path("type") curationType: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Query("lang") lang: String? = Locale.getDefault().language,
        @Query("countryCode") countryCode: String? = Locale.getDefault().country,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("limit") limit: Int = 12
    ): CurationPackageResponse

    @GET("package/{packageId}")
    suspend fun getStickerPackage(
        @Path("packageId") packageId: Int,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
    ): StickerPackageResponse

    @GET("search/keyword")
    suspend fun getRecommendedKeywords(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Query("lang") lang: String,
        @Query("countryCode") countryCode: String,
    ): KeywordListResponse

    @GET("package/send/{userId}")
    suspend fun getRecentlySentStickers(
        @Path("userId") userId: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userIdQuery: String = userId,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int
    ): StickerListResponse

    @GET("mysticker/favorite/{userId}")
    suspend fun getFavoriteStickers(
        @Path("userId") userId: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userIdQuery: String = userId,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int
    ): FavoriteListResponse

    @GET("mysticker/{userId}")
    suspend fun getMyStickers(
        @Path("userId") userId: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userIdQuery: String = userId,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int
    ): MyStickerResponse

    @GET("mysticker/hide/{userId}")
    suspend fun getMyHiddenStickers(
        @Path("userId") userId: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userIdQuery: String = userId,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int
    ): MyStickerResponse

    @PUT("mysticker/favorite/{userId}")
    suspend fun putMyStickerFavorite(
        @Path("userId") userId: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userIdQuery: String = userId,
        @Body favoriteBody: FavoriteBody
    ): StipopResponse

    @PUT("mysticker/order/{userId}")
    suspend fun putMyStickerOrders(
        @Path("userId") userId: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userIdQuery: String = userId,
        @Body orderChangeBody: OrderChangeBody
    ): MyStickerOrderChangedResponse

    @PUT("mysticker/hide/{userId}/{packageId}")
    suspend fun putMyStickerVisibility(
        @Path("userId") userId: String,
        @Path("packageId") packageId: Int,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userIdQuery: String = userId
    ): StipopResponse

    @GET("package")
    suspend fun getTrendingStickerPackages(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Query("lang") lang: String,
        @Query("countryCode") countryCode: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("q") query: String? = null
    ): StickerPackagesResponse

    @GET("search")
    suspend fun getStickers(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Query("lang") lang: String,
        @Query("countryCode") countryCode: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("q") query: String? = null
    ): StickersResponse

    @GET("package/new")
    suspend fun getNewStickerPackages(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Query("lang") lang: String,
        @Query("countryCode") countryCode: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("q") query: String? = null
    ): StickerPackagesResponse

    @POST("download/{packageId}")
    suspend fun postDownloadStickers(
        @Path("packageId") packageId: Int,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Query("isPurchase") isPurchase: String,
        @Query("countryCode") countryCode: String,
        @Query("lang") lang: String,
        @Query("price") price: Double? = null,
        @Query("entrance_point") entrancePoint: String? = null,
        @Query("event_point") eventPoint: String? = null,
    ): StipopResponse

    @POST("sdk/track/config")
    suspend fun trackConfig(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Body userIdBody: UserIdBody
    ): StipopResponse

    @POST("sdk/track/view/picker")
    suspend fun trackViewPicker(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Body userIdBody: UserIdBody
    ): Response<StipopResponse>

    @POST("sdk/track/view/search")
    suspend fun trackViewSearch(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Body userIdBody: UserIdBody
    ): Response<StipopResponse>

    @POST("sdk/track/view/store")
    suspend fun trackViewStore(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Body userIdBody: UserIdBody
    ): Response<StipopResponse>

    @POST("sdk/track/view/new")
    suspend fun trackViewNew(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Body userIdBody: UserIdBody
    ): Response<StipopResponse>

    @POST("sdk/track/view/mysticker")
    suspend fun trackViewMySticker(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Body userIdBody: UserIdBody
    ): Response<StipopResponse>

    @POST("sdk/track/view/package/{entrance_point}/{package_id}")
    suspend fun trackViewPackage(
        @Body userIdBody: UserIdBody,
        @Path("entrance_point") entrancePoint: String? = Constants.Point.DEFAULT,
        @Path("package_id") packageId: Int,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
    ): Response<StipopResponse>

    @POST("analytics/send/{stickerId}")
    suspend fun trackUsingSticker(
        @Path("stickerId") stickerId: String,
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Query("q") query: String? = null,
        @Query("countryCode") countryCode: String,
        @Query("lang") lang: String,
        @Query("event_point") eventPoint: String? = null
    ): StipopResponse

    @POST("analytics/error")
    suspend fun trackError(
        @Query("platform") platform: String = Constants.Value.PLATFORM,
        @Query("version") version: String = BuildConfig.SDK_VERSION_NAME,
        @Query("userId") userId: String,
        @Body trackErrorBody: TrackErrorBody
    ): Response<StipopResponse>

    companion object {

        private var accessTokenWithBearerText = "Bearer "

        private val loggingInterceptor = HttpLoggingInterceptor().apply { level = Level.HEADERS }
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

        fun create(): StipopApi {
            return Retrofit.Builder()
                .baseUrl(if (Constants.Value.IS_SANDBOX) Constants.Value.SANDBOX_URL else Constants.Value.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StipopApi::class.java)
        }
        private fun getHeaders(): Headers{
            return Headers.Builder()
                .add(
                    Constants.ApiParams.ApiKey, if (Constants.Value.IS_SANDBOX) {
                        Constants.Value.SANDBOX_APIKEY
                    } else {
                        Config.stipopConfigData.apiKey
                    }
                )
                .add(
                    Constants.ApiParams.SMetadata,
                    Gson().toJson(
                        StipopMetaHeader(
                            platform = Constants.Value.PLATFORM,
                            sdk_version = BuildConfig.SDK_VERSION_NAME,
                            os_version = Build.VERSION.SDK_INT.toString()
                        )
                    )
                )
                .add(
                    Constants.ApiParams.Authorization,
                    if (Config.sAuthIsActive) { accessTokenWithBearerText } else { "" }
                )
                .build()
        }
        fun setAccessToken(accessToken: String){
            accessTokenWithBearerText = "Bearer $accessToken"
        }
    }
}