package io.stipop.refactor.present.di

import dagger.Component
import io.stipop.Stipop
import io.stipop.activity.DetailActivity
import io.stipop.activity.KeyboardFragment
import io.stipop.activity.SearchActivity
import io.stipop.refactor.present.di.modules.NetworkModule
import io.stipop.refactor.present.ui.pages.store.MyPageFragment
import io.stipop.refactor.present.ui.pages.store.StoreActivity
import io.stipop.refactor.present.ui.pages.store.StorePageFragment
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
    fun inject(fragment: MyPageFragment)
    fun inject(activity: SearchActivity)
    fun inject(activity: DetailActivity)
    fun inject(fragment: KeyboardFragment)
    fun inject(instance: Stipop)
}
