package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.SearchRepository
import io.stipop.refactor.domain.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchStickerViewModel @Inject constructor(
    private val _userRepository: UserRepository,
    private val _searchRepository: SearchRepository,
) : SearchStickerViewModelProtocol {

    override val user: LiveData<SPUser>
        get() = _userRepository.user.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override val searchKeywordList: LiveData<List<SPKeywordItem>>
        get() = _searchRepository.searchKeywordList.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override val searchStickerList: LiveData<List<SPStickerItem>>
        get() = _searchRepository.searchStickerList.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override fun onChangeSearchKeyword(keyword: String?) {
        Log.d(
            this::class.simpleName, "onChangeSearchKeyword : \n" +
                    "keyword -> $keyword \n"
        )
        onLoadSearchStickerList(keyword)
    }

    override fun onLoadSearchKeywordList() {
        Log.d(
            this::class.simpleName, "onLoadSearchKeywordList : \n"
        )
        _userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                _searchRepository.trendingSearchTerms(user.apikey)
            }
        }
    }

    override fun onLoadSearchStickerList(keyword: String?, lastIndex: Int?) {
        Log.d(
            this::class.simpleName, "onLoadSearchStickerList : \n" +
                    "keyword -> $keyword \n" +
                    "lastIndex -> $lastIndex \n"
        )
        _userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                _searchRepository.stickerSearch(user.apikey, keyword ?: "", user.userId)
            }
        }
    }
}

interface SearchStickerViewModelProtocol {

    val user: LiveData<SPUser>
    val searchKeywordList: LiveData<List<SPKeywordItem>>
    val searchStickerList: LiveData<List<SPStickerItem>>

    fun onChangeSearchKeyword(keyword: String?)
    fun onLoadSearchKeywordList()
    fun onLoadSearchStickerList(keyword: String? = "", lastIndex: Int? = -1)
}
