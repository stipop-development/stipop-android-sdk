package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.data.repositories.*
import io.stipop.refactor.domain.repositories.*
import javax.inject.Singleton

@Module(includes = [DatasourceModule::class])
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindUserRepository(repository: UserDataRepository): UserRepository


    @Singleton
    @Binds
    fun bindMyStickersRepository(repository: MyStickersDataRepository): MyStickersRepository

    @Singleton
    @Binds
    fun bindSearchRepository(repository: SearchDataRepository): SearchRepository

    @Singleton
    @Binds
    fun bindStickerStoreRepository(repository: StickerStoreDataRepository): StickerStoreRepository

    @Singleton
    @Binds
    fun bindMyActiveStickersRepository(repository: MyActiveStickersDataRepository): MyActiveStickersRepository
}
