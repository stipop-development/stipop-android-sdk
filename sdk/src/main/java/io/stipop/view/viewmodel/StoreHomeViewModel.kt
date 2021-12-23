package io.stipop.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.data.PkgRepository
import io.stipop.delayedTextFlow
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class StoreHomeViewModel(private val repository: PkgRepository) : ViewModel() {

    inner class UiState{
        var isLoadingState = false
        var isSearchingState = false
    }

    var homeDataFlow: MutableLiveData<ArrayList<Any?>> = MutableLiveData()
    var uiStateFlow: MutableLiveData<UiState> = MutableLiveData()

    val typedQuery = MutableStateFlow("")
    val uiState = UiState()

    @ExperimentalCoroutinesApi
    @FlowPreview
    val emittedQuery: Flow<String> = typedQuery.debounce(200).mapLatest {
        if (it.isEmpty()) {
            return@mapLatest ""
        } else {
            return@mapLatest delayedTextFlow(it)
        }
    }


    fun flowQuery(keyword: String) {
        uiState.isSearchingState = keyword.isNotEmpty()
        typedQuery.value = keyword
    }

    fun getHomeSources() {
        viewModelScope.launch {
            combineTransform(
                repository.getRecommendQueryAsFlow(),
                repository.getCurationPackagesAsFlow("a"),
                repository.getCurationPackagesAsFlow("b")
            ) { value1, value2, value3 ->
                arrayListOf(value1?.body?.keywordList, value2?.body?.card, value3?.body?.card).run {
                    emit(this)
                }
            }.collectLatest {
                homeDataFlow.postValue(it)
            }
        }
    }

    fun loadsPackages(query: String? = null): Flow<PagingData<StickerPackage>> {
        uiState.apply {
            isSearchingState = !query.isNullOrEmpty()
        }.let {
            uiStateFlow.postValue(it)
        }
        return repository.getSearchingPackStream(query).cachedIn(viewModelScope)
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
}