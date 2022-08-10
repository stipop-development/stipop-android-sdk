package io.stipop.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.Stipop
import io.stipop.data.PkgRepository
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.models.StipopApiEnum
import io.stipop.s_auth.PostDownloadStickersEnum
import io.stipop.s_auth.SAuthManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

internal class StoreNewsViewModel(private val repository: PkgRepository) : ViewModel() {

    fun requestDownloadPackage(stickerPackage: StickerPackage) {
        viewModelScope.launch {
            try {
            repository.postDownloadStickers(stickerPackage) {
                it?.let { response ->
                    if (response.header.isSuccess()) {
                        PackageDownloadEvent.publishEvent(stickerPackage.packageId)
                    }
                }
            }
            } catch(exception: HttpException){
                when(exception.code()){
                    401 -> {
                        SAuthManager.setPostDownloadStickersData(PostDownloadStickersEnum.STORE_NEWS_VIEW_MODEL, stickerPackage)
                        Stipop.sAuthDelegate?.httpException(StipopApiEnum.POST_DOWNLOAD_STICKERS, exception)
                    }
                }
            }
        }
    }

    fun loadsPackages(): Flow<PagingData<StickerPackage>> {
        return repository.getNewPackStream().cachedIn(viewModelScope)
    }
}