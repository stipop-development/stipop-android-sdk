package io.stipop.base

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import io.stipop.api.StipopApi
import io.stipop.data.PackageRepository
import io.stipop.data.MyStickerRepository
import io.stipop.data.StickerDetailRepository
import io.stipop.view.viewmodel.StoreHomeViewModel
import io.stipop.view.viewmodel.MyStickerViewModel
import io.stipop.view.viewmodel.NewStickerViewModel
import io.stipop.view.viewmodel.PackageDetailViewModel

internal object Injection {

    private val stipopApi = StipopApi.create()

    private fun provideMyStickerRepository(): MyStickerRepository {
        return MyStickerRepository(stipopApi)
    }

    private fun providePackageRepository(): PackageRepository {
        return PackageRepository(stipopApi)
    }

    private fun provideStickerDetailRepository(): StickerDetailRepository {
        return StickerDetailRepository(stipopApi)
    }

    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner)
    }

    class ViewModelFactory(
        owner: SavedStateRegistryOwner
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            if (modelClass.isAssignableFrom(MyStickerViewModel::class.java)) {
                return MyStickerViewModel(provideMyStickerRepository()) as T
            } else if (modelClass.isAssignableFrom(StoreHomeViewModel::class.java)) {
                return StoreHomeViewModel(providePackageRepository()) as T
            } else if (modelClass.isAssignableFrom(NewStickerViewModel::class.java)) {
                return NewStickerViewModel(providePackageRepository()) as T
            }  else if (modelClass.isAssignableFrom(PackageDetailViewModel::class.java)) {
                return PackageDetailViewModel(provideStickerDetailRepository()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


