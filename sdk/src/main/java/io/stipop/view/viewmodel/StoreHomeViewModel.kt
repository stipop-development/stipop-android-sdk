package io.stipop.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.data.PackageRepository
import io.stipop.delayedTextFlow
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class StoreHomeViewModel(private val repository: PackageRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    var dataSet: MutableLiveData<ArrayList<List<StickerPackage>>> = MutableLiveData()
    var uiState: MutableLiveData<Boolean> = MutableLiveData()

    fun flowQuery(keyword: String) {
        _searchQuery.value = keyword
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    val emittedQuery: Flow<String> = _searchQuery.debounce(300).mapLatest {
        if (it.isEmpty()) {
            return@mapLatest ""
        } else {
            return@mapLatest delayedTextFlow(it)
        }
    }

    fun getHomes() {
        viewModelScope.launch {
            combineTransform(
                repository.getPackagesAsFlow(1),
                repository.getPackagesAsFlow(2),
                repository.getPackagesAsFlow(3)
            ) { value1, value2, value3 ->
                val lists = arrayListOf(
                    value1.body.packageList,
                    value2.body.packageList,
                    value3.body.packageList
                )
                emit(lists)
            }.collect {
                dataSet.postValue(it)
            }
        }
    }

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

    fun loadsPackages(query: String? = null): Flow<PagingData<StickerPackage>> {
        uiState.postValue(!query.isNullOrEmpty())
        return repository.getHomeStickerPackageStream(query).cachedIn(viewModelScope)
    }
}