package io.stipop.refactor.present.di

import dagger.Component
import io.stipop.Stipop
import io.stipop.refactor.present.di.modules.ViewModelBindsModule
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboard
import io.stipop.refactor.present.ui.pages.search_sticker.SPSearchStickerActivity
import io.stipop.refactor.present.ui.pages.store.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelBindsModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(activity: SPStoreActivity)
    fun inject(fragment: SPStorePageFragment)
    fun inject(fragment: SPStoreSearchPackageFragment)
    fun inject(fragment: SPStorePackageFragment)
    fun inject(fragment: SPMyPageFragment)
    fun inject(fragment: SPMyActivePackageFragment)
    fun inject(fragment: SPMyHiddenPackageFragment)
    fun inject(activity: SPSearchStickerActivity)
    fun inject(activity: SPDetailActivity)
    fun inject(view: SPStickerKeyboard)
    fun inject(stipop: Stipop)
}
