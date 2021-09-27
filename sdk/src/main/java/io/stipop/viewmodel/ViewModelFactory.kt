package io.stipop.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import io.stipop.data.MyStickerRepository

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: MyStickerRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(MyStickerRepositoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyStickerRepositoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
