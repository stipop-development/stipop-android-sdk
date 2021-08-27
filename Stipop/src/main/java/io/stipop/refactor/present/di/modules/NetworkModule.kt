package io.stipop.refactor.present.di.modules

import dagger.Module
import dagger.Provides
import io.stipop.refactor.data.services.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    private val _apiClient = Retrofit.Builder()
        .baseUrl("https://messenger.stipop.io/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideMyStickersService(): MyStickersService {
        return _apiClient
            .create(MyStickersService::class.java)
    }

    @Singleton
    @Provides
    fun provideSearchService(): SearchService {
        return _apiClient
            .create(SearchService::class.java)
    }

    @Singleton
    @Provides
    fun provideStickerStoreService(): StickerStoreService {
        return _apiClient
            .create(StickerStoreService::class.java)
    }
}
