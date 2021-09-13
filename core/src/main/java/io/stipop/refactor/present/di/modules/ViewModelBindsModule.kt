package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.present.ui.view_models.*

@Module(
    includes = [
        RepositoryBindsModule::class,
        BlocBindsModule::class
    ]
)
interface ViewModelBindsModule {

    @Binds
    fun bindStipopViewModel(viewModel: StipopViewModelV1): StipopViewModel

    @Binds
    fun bindDetailViewModel(viewModel: DetailViewModelV1): DetailViewModel

    @Binds
    fun bindMyPageViewModel(viewModel: MyPageViewModelV1): MyPageViewModel

    @Binds
    fun bindSearchStickerViewModel(viewModel: SearchStickerViewModelV1): SearchStickerViewModel

    @Binds
    fun bindStickerKeyboardViewModel(viewModel: StickerKeyboardViewModelV1): StickerKeyboardViewModel

    @Binds
    fun bindStorePageViewModel(viewModel: StorePageViewModelV1): StorePageViewModel

    @Binds
    fun bindStoreViewModel(viewModel: StoreViewModelV1): StoreViewModel

    @Binds
    fun bindStoreAllPackageViewModel(viewModel: StoreAllPackageViewModelV1): StoreAllPackageViewModel

    @Binds
    fun bindStoreSearchPackageViewModel(viewModel: StoreSearchPackageViewModelV1): StoreSearchPackageViewModel

}
