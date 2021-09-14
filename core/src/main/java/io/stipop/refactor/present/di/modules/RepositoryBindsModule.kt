package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.data.repositories.*
import io.stipop.refactor.domain.repositories.*
import io.stipop.refactor.domain.repositories.RecentlySentStickersRepository
import javax.inject.Singleton

@Module(includes = [
    DatasourceBindsModule::class,
    ServiceProvidesModule::class,
])
interface RepositoryBindsModule {

    @Singleton
    @Binds
    fun bindMyStickersRepository(repository: MyStickersDataRepository): MyStickersRepository



    @Singleton
    @Binds
    fun bindUserRepository(repository: UserDataRepository): UserRepository

    @Singleton
    @Binds
    fun bindSearchStickerRepository(repository: SearchStickerDataRepository): SearchStickerRepository

    @Singleton
    @Binds
    fun bindMyActivePackageRepository(repository: MyActivePackageDataRepository): MyActivePackageRepository

    @Singleton
    @Binds
    fun bindMyHiddenPackageRepository(repository: MyHiddenPackageDataRepository): MyHiddenPackageRepository

    @Singleton
    @Binds
    fun bindRecentlySentStickersRepository(repository: RecentlySentStickersDataRepository): RecentlySentStickersRepository

    @Singleton
    @Binds
    fun bindSearchKeywordRepository(repository: SearchKeywordDataRepository): SearchKeywordRepository

    @Singleton
    @Binds
    fun bindAllPackageRepository(repository: StorePackageDataRepository): StorePackageRepository

    @Singleton
    @Binds
    fun bindSearchPackageRepository(repository: StoreSearchPackageDataRepository): StoreSearchPackageRepository
}
