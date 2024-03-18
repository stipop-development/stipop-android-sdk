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
import io.stipop.models.enums.StipopApiEnum
import io.stipop.s_auth.GetRecommendedKeywordsEnum
import io.stipop.s_auth.SAuthManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.HttpException

internal class SsvModel(private val repository: SearchingRepository) : ViewModel() {

    private val taskScope = CoroutineScope(Job() + Dispatchers.IO)
    private val typedQuery = MutableStateFlow("")
    var uiState: MutableLiveData<Boolean> = MutableLiveData()
    var homeDataFlow: MutableLiveData<ArrayList<Any?>> = MutableLiveData()

    init {
        trackViewSearch()
    }

    internal fun trackViewSearch(){
        taskScope.launch {
            try {
                repository.safeCall(
                    call = { StipopApi.create().trackViewSearch(userIdBody = UserIdBody(userId = Stipop.userId)) }, onCompletable = {
                        when (it?.code()) {
                            401 -> Stipop.sAuthDelegate?.httpException(StipopApiEnum.TRACK_VIEW_SEARCH, HttpException(it))
                        }
                    })
            } catch(exception: Exception){
                Stipop.trackError(exception)
            }
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
            try {
                repository.getRecommendQueryAsFlow().collect {
                    homeDataFlow.postValue(arrayListOf(it?.body?.keywordList))
                }
            } catch(exception: HttpException){
                when(exception.code()){
                    401 -> {
                        SAuthManager.setGetRecommendedKeywordsData(GetRecommendedKeywordsEnum.STICKER_SEARCH_VIEW)
                        Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_RECOMMENDED_KEYWORDS, exception)
                    }
                }
            } catch(exception: Exception){
                Stipop.trackError(exception)
            }
        }
    }
}