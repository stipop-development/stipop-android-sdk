package io.stipop.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.stipop.PackUtils
import io.stipop.custom.PagingRecyclerView
import io.stipop.data.AllStickerRepository
import io.stipop.delayedTextFlow
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class AllStickerViewModel(private val repository: AllStickerRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    var query: String? = null
    var recyclerView: PagingRecyclerView? = null
    var stickerPackages: MutableLiveData<List<StickerPackage>> = MutableLiveData()
    var clearAction: MutableLiveData<Boolean> = MutableLiveData()

    fun flowQuery(keyword: String) {
        _searchQuery.value = keyword
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    val emittedQuery: Flow<String> = _searchQuery.debounce(600).mapLatest {
        if (it.isEmpty()) {
            return@mapLatest ""
        } else {
            return@mapLatest delayedTextFlow(it)
        }
    }

    fun searchQuery(keyword: String) {
        query = keyword
        refreshData(query)
    }

    fun refreshData(query: String? = null) {
        val isSearchView = !query.isNullOrEmpty()
        clearAction.postValue(isSearchView)
        recyclerView?.refresh()
    }

    fun registerRecyclerView(pagingRecyclerView: PagingRecyclerView?) {
        recyclerView = pagingRecyclerView
        viewModelScope.launch {
            recyclerView?.paging?.collectLatest {
                getPackages(it)
            }
        }
    }

    fun requestDownloadPackage(stickerPackage: StickerPackage) {
        viewModelScope.launch {
            repository.postDownloadStickers(stickerPackage) {
                PackUtils.downloadAndSaveLocalV2(stickerPackage) {
                    PackageDownloadEvent.publishEvent(stickerPackage.packageId)
                }
            }
        }
    }

    private fun getPackages(page: Int) {
        viewModelScope.launch {
            repository.getStickerPackages(page, query, onSuccess = {
                stickerPackages.postValue(it as List<StickerPackage>)
            })
        }
    }
}