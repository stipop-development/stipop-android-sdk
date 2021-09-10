package io.stipop.refactor.present.di

import dagger.Component
import io.stipop.Stipop
import io.stipop.refactor.present.di.modules.ViewModelModule
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboard
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
    fun inject(view: SPStickerKeyboard)
    fun inject(stipop: Stipop)
}
