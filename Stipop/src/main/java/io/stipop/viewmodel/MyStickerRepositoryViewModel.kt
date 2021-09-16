package io.stipop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.data.MyStickerRepository
import io.stipop.models.StickerPackage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MyStickerRepositoryViewModel(private val repository: MyStickerRepository) : ViewModel() {

    private var isWantVisibleStickers: Boolean = true
    private var visibleMyStickers: Flow<PagingData<StickerPackage>>? = null

    fun loadStickers(wantVisibleSticker: Boolean): Flow<PagingData<StickerPackage>> {
        isWantVisibleStickers = wantVisibleSticker
        val newResult: Flow<PagingData<StickerPackage>> =
            repository.getMyStickerStream(isWantVisibleStickers).cachedIn(viewModelScope)
        visibleMyStickers = newResult
        return newResult
    }

    fun updateOrder(fromStickerPackage: StickerPackage, toStickerPackage: StickerPackage) =
        viewModelScope.launch {
            repository.request(fromStickerPackage, toStickerPackage)
        }

}