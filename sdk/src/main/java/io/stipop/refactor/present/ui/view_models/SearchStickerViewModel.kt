package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.SearchKeywordRepository
import io.stipop.refactor.domain.repositories.SearchStickerRepository
import io.stipop.refactor.domain.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SearchStickerViewModel @Inject constructor(
    private val _userRepository: UserRepository,
    private val _searchStickerRepository: SearchStickerRepository,
    private val _searchKeywordRepository: SearchKeywordRepository,
) : SearchStickerViewModelProtocol {

    private var _keyword: String = ""

    override val user: LiveData<SPUser>
        get() = _userRepository.user.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    val _selectedKeyword: MutableLiveData<SPKeywordItem?> = MutableLiveData()
    override val selectedKeyword: LiveData<SPKeywordItem?>
        get() = _selectedKeyword

    val _selectedSticker: MutableLiveData<SPStickerItem?> = MutableLiveData()
    override val selectedSticker: LiveData<SPStickerItem?>
        get() = _selectedSticker

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
            _keyword = keyword
            onLoadSearchStickerList(-1)
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

    override fun onLoadSearchStickerList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadSearchStickerList : \n" +
                    "keyword -> $_keyword \n" +
                    "index -> $index \n"
        )
        runBlocking(Dispatchers.IO) {
            _userRepository.currentUser?.let { user ->
                _searchStickerRepository.onLoadMoreList(user, _keyword, index)
            }
        }

    }

    override fun onSelectStickerItem(item: SPStickerItem?) {
        _selectedSticker.postValue(item)
    }

    override fun onSelectKeyword(item: SPKeywordItem?) {
        _selectedKeyword.postValue(item)
    }
}

interface SearchStickerViewModelProtocol {

    val user: LiveData<SPUser>
    val selectedKeyword: LiveData<SPKeywordItem?>
    val selectedSticker: LiveData<SPStickerItem?>
    val searchKeywordList: LiveData<List<SPKeywordItem>>
    val searchStickerList: LiveData<List<SPStickerItem>>

    fun onChangeSearchKeyword(keyword: String?)
    fun onLoadSearchKeywordList(index: Int)
    fun onLoadSearchStickerList(index: Int)
    fun onSelectStickerItem(item: SPStickerItem?)
    fun onSelectKeyword(item: SPKeywordItem?)
}
