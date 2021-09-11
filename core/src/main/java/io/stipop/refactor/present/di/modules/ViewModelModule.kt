package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModelV1
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModel
import io.stipop.refactor.present.ui.view_models.StickerKeyboardViewModel
import io.stipop.refactor.present.ui.view_models.StickerKeyboardViewModelV1
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class])
interface ViewModelModule {

    @Singleton
    @Binds
    fun bindSearchStickerViewModel(viewModel: SearchStickerViewModelV1): SearchStickerViewModel

    @Singleton
    @Binds
    fun bindStickerKeyboardViewModel(viewModel: StickerKeyboardViewModelV1): StickerKeyboardViewModel
}
