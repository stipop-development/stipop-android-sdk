package io.stipop.refactor.present.di.modules

import dagger.Module
import dagger.Provides
import io.stipop.refactor.data.services.*
import io.stipop.refactor.domain.services.MyStickersService
import io.stipop.refactor.domain.services.SearchService
import io.stipop.refactor.domain.services.StickerStoreService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ServiceModule {

    private val _httpClient = with(OkHttpClient.Builder()) {
        addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        build()
    }

    private val _apiClient = Retrofit.Builder()
        .baseUrl("https://messenger.stipop.io/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(_httpClient)
        .build()

    @Singleton
    @Provides
    fun provideMyStickersService(): MyStickersService {
        return _apiClient
            .create(MyStickersRestService::class.java)
    }

    @Singleton
    @Provides
    fun provideSearchService(): SearchService {
        return _apiClient
            .create(SearchRestService::class.java)
    }

    @Singleton
    @Provides
    fun provideStickerStoreService(): StickerStoreService {
        return _apiClient
            .create(StickerStoreRestService::class.java)
    }
}
