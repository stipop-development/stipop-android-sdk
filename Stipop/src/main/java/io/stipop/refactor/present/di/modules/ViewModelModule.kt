package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModel
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModelProtocol

@Module(includes = [RepositoryModule::class])
interface ViewModelModule {

    @Binds
    fun bindSearchStickerViewModel(viewModel: SearchStickerViewModel): SearchStickerViewModelProtocol
}
