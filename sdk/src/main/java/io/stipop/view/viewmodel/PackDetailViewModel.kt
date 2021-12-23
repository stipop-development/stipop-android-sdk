package io.stipop.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.stipop.StipopUtils
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.data.StickerDetailRepository
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.models.body.UserIdBody
import kotlinx.coroutines.launch

internal class PackDetailViewModel(private val repository: StickerDetailRepository) :
    ViewModel() {

    var stickerPackage: MutableLiveData<StickerPackage?> = MutableLiveData()

    fun trackViewPackage(packageId: Int, entrancePoint: String?) {
        viewModelScope.launch {
            StipopApi.create().trackViewPackage(
                UserIdBody(Stipop.userId),
                entrancePoint = entrancePoint,
                packageId = packageId
            )
        }
    }

    fun loadsPackages(packageId: Int) {
        viewModelScope.launch {
            repository.getStickerPackage(packageId, onSuccess = {
                if (it.header.isSuccess()) {
                    stickerPackage.postValue(it.body?.stickerPackage)
                }
            })
        }
    }

    fun requestDownloadPackage() {
        viewModelScope.launch {
            stickerPackage.value?.let { stickerPackage ->
                if (!stickerPackage.isDownloaded()) {
                    repository.postDownloadStickers(stickerPackage) {
                        it?.let { response ->
                            if (response.header.isSuccess()) {
                                PackageDownloadEvent.publishEvent(stickerPackage.packageId)
                            }
                        }
                    }
                }

            }
        }
    }
}