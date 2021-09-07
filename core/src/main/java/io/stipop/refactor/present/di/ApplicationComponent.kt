package io.stipop.refactor.present.di

import dagger.Component
import io.stipop.Stipop
import io.stipop.refactor.present.di.modules.DatasourceModule
import io.stipop.refactor.present.di.modules.RepositoryModule
import io.stipop.refactor.present.di.modules.ServiceModule
import io.stipop.refactor.present.di.modules.ViewModelModule
import io.stipop.refactor.present.ui.components.common.KeyboardPackagePresenter
import io.stipop.refactor.present.ui.components.common.KeyboardStickerPresenter
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboardPopupWindow
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboardPresenter
import io.stipop.refactor.present.ui.pages.search_sticker.SPSearchStickerActivity
import io.stipop.refactor.present.ui.pages.store.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(activity: SPStoreActivity)
    fun inject(fragment: StorePageFragment)
    fun inject(fragment: StoreSearchPackageListFragment)
    fun inject(fragment: StoreAllPackageListFragment)
    fun inject(fragment: SPMyPageFragment)
    fun inject(fragment: SPMyActivePackageListFragment)
    fun inject(fragment: SPMyHiddenPackageListFragment)
    fun inject(activity: SPSearchStickerActivity)
    fun inject(activity: SPDetailActivity)
    fun inject(instance: Stipop)
    fun inject(window: SPStickerKeyboardPopupWindow)
    fun inject(presenter: KeyboardPackagePresenter)
    fun inject(presenter: KeyboardStickerPresenter)
    fun inject(present: SPStickerKeyboardPresenter)
}
