package io.stipop.refactor.present.di

import dagger.Component
import io.stipop.Stipop
import io.stipop.activity.DetailActivity
import io.stipop.refactor.present.ui.components.common.SPKeyboardFragment
import io.stipop.refactor.present.ui.pages.search_sticker.SearchActivity
import io.stipop.refactor.present.di.modules.NetworkModule
import io.stipop.refactor.present.ui.pages.store.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(activity: StoreActivity)
    fun inject(fragment: StorePageFragment)
    fun inject(fragment: StoreSearchPackageListFragment)
    fun inject(fragment: StoreAllPackageListFragment)
    fun inject(fragment: MyPageFragment)
    fun inject(fragment: MyActivePackageListFragment)
    fun inject(fragment: MyHiddenPackageListFragment)
    fun inject(activity: SearchActivity)
    fun inject(activity: DetailActivity)
    fun inject(fragment: SPKeyboardFragment)
    fun inject(instance: Stipop)
}
