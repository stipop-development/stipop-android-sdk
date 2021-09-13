package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.SearchKeywordRepository
import io.stipop.refactor.domain.repositories.SearchStickerRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject

class SearchStickerViewModelV1 @Inject constructor(
    private val _userRepository: UserRepository,
    private val _searchStickerRepository: SearchStickerRepository,
    private val _searchKeywordRepository: SearchKeywordRepository,
) : SearchStickerViewModel {

    private var _keyword: String = ""

    override val user: LiveData<SPUser>
        get() = _userRepository.userChanges

    private val _selectedKeyword: MutableLiveData<SPKeywordItem?> = MutableLiveData()
    override val selectedKeyword: LiveData<SPKeywordItem?>
        get() = _selectedKeyword

    private val _selectedSticker: MutableLiveData<SPStickerItem?> = MutableLiveData()
    override val selectedSticker: LiveData<SPStickerItem?>
        get() = _selectedSticker

    override val keywordList: LiveData<List<SPKeywordItem>>
        get() = _searchKeywordRepository.listChanges

    override val stickerList: LiveData<List<SPStickerItem>>
        get() = _searchStickerRepository.listChanges

    override fun onChangeKeyword(keyword: String?) {
        Log.d(
            this::class.simpleName, "onChangeSearchKeyword : \n" +
                    "keyword -> $keyword \n"
        )
        keyword?.let {
            _keyword = keyword
            onLoadStickerList(-1)
        }
    }

    override fun onLoadKeywordList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadSearchKeywordList : \n"
        )
        _userRepository.currentUser?.let { user ->
            _searchKeywordRepository.onLoadMoreList(user, "", index)
        }
    }

    override fun onLoadStickerList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadSearchStickerList : \n" +
                    "keyword -> $_keyword \n" +
                    "index -> $index \n"
        )
        _userRepository.currentUser?.let { user ->
            _searchStickerRepository.onLoadMoreList(user, _keyword, index)
        }

    }

    override fun onSelectStickerItem(item: SPStickerItem?) {
        _selectedSticker.postValue(item)
    }

    override fun onSelectKeyword(item: SPKeywordItem?) {
        _selectedKeyword.postValue(item)
    }
}

interface SearchStickerViewModel {

    val user: LiveData<SPUser>
    val selectedKeyword: LiveData<SPKeywordItem?>
    val selectedSticker: LiveData<SPStickerItem?>
    val keywordList: LiveData<List<SPKeywordItem>>
    val stickerList: LiveData<List<SPStickerItem>>

    fun onChangeKeyword(keyword: String?)
    fun onLoadKeywordList(index: Int)
    fun onLoadStickerList(index: Int)
    fun onSelectStickerItem(item: SPStickerItem?)
    fun onSelectKeyword(item: SPKeywordItem?)
}
