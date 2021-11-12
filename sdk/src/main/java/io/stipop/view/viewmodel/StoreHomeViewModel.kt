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

    private val typedQuery = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    @FlowPreview
    val emittedQuery: Flow<String> = typedQuery.debounce(300).mapLatest {
        if (it.isEmpty()) {
            return@mapLatest ""
        } else {
            return@mapLatest delayedTextFlow(it)
        }
    }
    var homeDataFlow: MutableLiveData<ArrayList<Any?>> = MutableLiveData()
    var uiState: MutableLiveData<Boolean> = MutableLiveData()

    fun flowQuery(keyword: String) {
        typedQuery.value = keyword
    }

    fun getHomeSources() {
        viewModelScope.launch {
            combineTransform(
                repository.getRecommendQueryAsFlow(),
                repository.getCurationPackagesAsFlow("a"),
                repository.getCurationPackagesAsFlow("b")
            ) { value1, value2, value3 ->
                val lists = arrayListOf(
                    value1?.body?.keywordList,
                    value2?.body?.card,
                    value3?.body?.card
                )
                emit(lists)
            }.collect {
                homeDataFlow.postValue(it)
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