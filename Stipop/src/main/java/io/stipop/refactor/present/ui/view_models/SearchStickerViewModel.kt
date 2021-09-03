package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.SearchKeywordRepository
import io.stipop.refactor.domain.repositories.SearchStickerRepository
import io.stipop.refactor.domain.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SearchStickerViewModel @Inject constructor(
    private val _userRepository: UserRepository,
    private val _searchStickerRepository: SearchStickerRepository,
    private val _searchKeywordRepository: SearchKeywordRepository,
) : SearchStickerViewModelProtocol {

    override val user: LiveData<SPUser>
        get() = _userRepository.user.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override val searchKeywordList: LiveData<List<SPKeywordItem>>
        get() = _searchKeywordRepository.listChanges.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override val searchStickerList: LiveData<List<SPStickerItem>>
        get() = _searchStickerRepository.listChanges.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override fun onChangeSearchKeyword(keyword: String?) {
        Log.d(
            this::class.simpleName, "onChangeSearchKeyword : \n" +
                    "keyword -> $keyword \n"
        )
        keyword?.let {
            onLoadSearchStickerList(it, 0)
        }
    }

    override fun onLoadSearchKeywordList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadSearchKeywordList : \n"
        )
        runBlocking(Dispatchers.IO) {
            _userRepository.currentUser?.let { user ->
                _searchKeywordRepository.onLoadMoreList(user, "", index)
            }
        }
    }

    override fun onLoadSearchStickerList(keyword: String, index: Int) {
        Log.d(
            this::class.simpleName, "onLoadSearchStickerList : \n" +
                    "keyword -> $keyword \n" +
                    "index -> $index \n"
        )
        runBlocking(Dispatchers.IO) {
            _userRepository.currentUser?.let { user ->
                _searchStickerRepository.onLoadMoreList(user, keyword, index)
            }
        }

    }
}

interface SearchStickerViewModelProtocol {

    val user: LiveData<SPUser>
    val searchKeywordList: LiveData<List<SPKeywordItem>>
    val searchStickerList: LiveData<List<SPStickerItem>>

    fun onChangeSearchKeyword(keyword: String?)
    fun onLoadSearchKeywordList(index: Int)
    fun onLoadSearchStickerList(keyword: String, index: Int)
}
