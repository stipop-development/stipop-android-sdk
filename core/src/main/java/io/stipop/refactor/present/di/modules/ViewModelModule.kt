package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.present.ui.view_models.*

@Module(includes = [RepositoryModule::class])
interface ViewModelModule {

    @Binds
    fun bindSearchStickerViewModel(viewModel: SearchStickerViewModelV1): SearchStickerViewModel

    @Binds
    fun bindStickerKeyboardViewModel(viewModel: StickerKeyboardViewModelV1): StickerKeyboardViewModel

    @Binds
    fun bindStorePageViewModel(viewModel: StorePageViewModelV1): StorePageViewModel
}
