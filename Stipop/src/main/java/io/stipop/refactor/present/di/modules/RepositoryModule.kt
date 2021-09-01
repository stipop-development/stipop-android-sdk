package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.data.repositories.MyStickersDataRepository
import io.stipop.refactor.data.repositories.SearchDataRepository
import io.stipop.refactor.data.repositories.StickerStoreDataRepository
import io.stipop.refactor.data.repositories.UserDataRepository
import io.stipop.refactor.domain.repositories.MyStickersRepository
import io.stipop.refactor.domain.repositories.SearchRepository
import io.stipop.refactor.domain.repositories.StickerStoreRepository
import io.stipop.refactor.domain.repositories.UserRepository
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
}
