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

@Module(includes = [ServiceProvidesModule::class])
interface DatasourceBindsModule {
    @Binds
    fun bindMyStickersDatasource(datasource: MyStickersRestDatasource): MyStickersDatasource

    @Binds
    fun bindSearchDatasource(datasource: SearchRestDatasource): SearchDatasource

    @Binds
    fun bindStickerStoreDatasource(datasource: StickerStoreRestDatasource): StickerStoreDatasource
}
