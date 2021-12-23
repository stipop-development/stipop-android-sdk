package io.stipop.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.data.SearchingRepository
import io.stipop.delayedTextFlow
import io.stipop.models.Sticker
import io.stipop.models.body.UserIdBody
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class SsvModel(private val repository: SearchingRepository) : ViewModel() {

    private val taskScope = CoroutineScope(Job() + Dispatchers.IO)
    private val typedQuery = MutableStateFlow("")
    var uiState: MutableLiveData<Boolean> = MutableLiveData()
    var homeDataFlow: MutableLiveData<ArrayList<Any?>> = MutableLiveData()

    init {
        taskScope.launch {
            StipopApi.create().trackViewSearch(UserIdBody(Stipop.userId))
        }
    }

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
        typedQuery.value = keyword
    }

    fun loadStickers(query: String? = null): Flow<PagingData<Sticker>> {
        uiState.postValue(!query.isNullOrEmpty())
        return repository.getStickersStream(query).cachedIn(viewModelScope)
    }

    fun getKeywords() {
        viewModelScope.launch {
            repository.getRecommendQueryAsFlow().collect {
                homeDataFlow.postValue(arrayListOf(it?.body?.keywordList))
            }
        }
    }

}