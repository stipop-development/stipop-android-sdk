package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.data.datasources.MyStickersRestDatasource
import io.stipop.refactor.data.datasources.SearchRestDatasource
import io.stipop.refactor.data.datasources.StickerStoreRestDatasource
import io.stipop.refactor.domain.datasources.MyStickersDatasource
import io.stipop.refactor.domain.datasources.SearchDatasource
import io.stipop.refactor.domain.datasources.StickerStoreDatasource
import javax.inject.Singleton

@Module(includes = [ServiceModule::class])
interface DatasourceModule {

    @Singleton
    @Binds
    fun provideMyStickersDatasource(datasource: MyStickersRestDatasource): MyStickersDatasource

    @Singleton
    @Binds
    fun provideSearchDatasource(datasource: SearchRestDatasource): SearchDatasource

    @Singleton
    @Binds
    fun provideStickerStoreDatasource(datasource: StickerStoreRestDatasource): StickerStoreDatasource
}
