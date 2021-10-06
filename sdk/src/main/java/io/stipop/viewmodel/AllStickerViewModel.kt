package io.stipop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.stipop.data.AllStickerRepository
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import kotlinx.coroutines.launch

class AllStickerViewModel(private val repository: AllStickerRepository) : ViewModel() {

    private var page = 1
    private var keyword: String? = null
    var stickerPackages: MutableLiveData<List<StickerPackage>> = MutableLiveData()

    fun getPackages(){
        viewModelScope.launch {
            repository.getStickerPackages(page, keyword, onSuccess = {
                val list = it as List<StickerPackage>
                stickerPackages.postValue(list)
            })
        }
    }
}