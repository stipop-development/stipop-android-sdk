package io.stipop.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.stipop.PackUtils
import io.stipop.custom.PagingRecyclerView
import io.stipop.data.AllStickerRepository
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllStickerViewModel(private val repository: AllStickerRepository) : ViewModel() {

    private var keyword: String? = null
    var stickerPackages: MutableLiveData<List<StickerPackage>> = MutableLiveData()

    fun registerRecyclerView(pagingRecyclerView: PagingRecyclerView?){
        getPackages(1)
        viewModelScope.launch {
            pagingRecyclerView?.paging?.collectLatest {
                Log.d("STIPOP-DEBUG", "PAGING : $it")
                getPackages(it)
            }
        }
    }

    fun requestDownloadPackage(stickerPackage: StickerPackage){
        viewModelScope.launch {
            repository.postDownloadStickers(stickerPackage) {
                PackUtils.downloadAndSaveLocalV2(stickerPackage) {
                    PackageDownloadEvent.publishEvent(stickerPackage.packageId)
                }
            }
        }
    }

    private fun getPackages(page: Int){
        viewModelScope.launch {
            repository.getStickerPackages(page, keyword, onSuccess = {
                stickerPackages.postValue(it as List<StickerPackage>)
            })
        }
    }
}