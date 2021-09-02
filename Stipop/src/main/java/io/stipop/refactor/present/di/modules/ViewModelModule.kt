package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModel
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModelProtocol
import io.stipop.refactor.present.ui.view_models.KeyboardViewModel
import io.stipop.refactor.present.ui.view_models.StickerKeyboardViewModelV1

@Module(includes = [RepositoryModule::class])
interface ViewModelModule {

    @Binds
    fun bindSearchStickerViewModel(viewModel: SearchStickerViewModel): SearchStickerViewModelProtocol

    @Binds
    fun bindStickerKeyboardViewModel(viewModel: StickerKeyboardViewModelV1): KeyboardViewModel
}
