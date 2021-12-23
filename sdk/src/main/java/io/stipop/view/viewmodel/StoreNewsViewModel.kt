package io.stipop.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.data.PkgRepository
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class StoreNewsViewModel(private val repository: PkgRepository) : ViewModel() {

    fun requestDownloadPackage(stickerPackage: StickerPackage) {
        viewModelScope.launch {
            repository.postDownloadStickers(stickerPackage) {
                it?.let { response ->
                    if (response.header.isSuccess()) {
                        PackageDownloadEvent.publishEvent(stickerPackage.packageId)
                    }
                }
            }
        }
    }

    fun loadsPackages(): Flow<PagingData<StickerPackage>> {
        return repository.getNewPackStream().cachedIn(viewModelScope)
    }
}