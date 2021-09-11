package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.data.repositories.*
import io.stipop.refactor.data.repositories.StickerPackInfoDataRepository
import io.stipop.refactor.domain.repositories.*
import io.stipop.refactor.domain.repositories.StickerPackInfoRepository
import io.stipop.refactor.domain.repositories.RecentlySentStickersRepository
import javax.inject.Singleton

@Module(includes = [
    DatasourceModule::class,
    ServiceModule::class,
])
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindUserRepository(repository: UserDataRepository): UserRepository

    @Singleton
    @Binds
    fun bindMyStickersRepository(repository: MyStickersDataRepository): MyStickersRepository

    @Singleton
    @Binds
    fun bindSearchRepository(repository: SearchStickerDataRepository): SearchStickerRepository

    @Singleton
    @Binds
    fun bindStickerStoreRepository(repository: StickerStoreDataRepository): StickerStoreRepository

    @Singleton
    @Binds
    fun bindMyActiveStickersRepository(repository: MyActiveStickersDataRepository): MyActiveStickersRepository

    @Singleton
    @Binds
    fun bindStickerPackInfoRepository(repository: StickerPackInfoDataRepository): StickerPackInfoRepository

    @Singleton
    @Binds
    fun bindRecentlySentStickersRepository(repository: RecentlySentStickersDataRepository): RecentlySentStickersRepository

    @Singleton
    @Binds
    fun bindSearchKeywordRepository(repository: SearchKeywordDataRepository): SearchKeywordRepository

    @Singleton
    @Binds
    fun bindAllPackageRepository(repository: StoreAllPackageDataRepository): StoreAllPackageRepository

    @Singleton
    @Binds
    fun bindSearchPackageRepository(repository: StoreSearchPackageDataRepository): StoreSearchPackageRepository
}
