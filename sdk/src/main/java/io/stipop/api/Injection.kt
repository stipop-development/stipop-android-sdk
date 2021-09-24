package io.stipop.api

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import io.stipop.data.MyStickerRepository
import io.stipop.viewmodel.ViewModelFactory

object Injection {

    private fun provideMyStickerRepository(): MyStickerRepository {
        return MyStickerRepository(StipopApi.create())
    }

    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideMyStickerRepository())
    }

}
