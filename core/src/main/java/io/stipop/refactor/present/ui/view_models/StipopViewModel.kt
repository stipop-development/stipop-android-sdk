package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPStickerItem
import javax.inject.Inject

interface StipopViewModel {
    val selectStickerChanges: LiveData<SPStickerItem?>
    fun onSelectStickerItem(item: SPStickerItem?)
}

class StipopViewModelV1 @Inject constructor() : StipopViewModel {

    private val _selectStickerChanges: MutableLiveData<SPStickerItem?> = MutableLiveData()
    override val selectStickerChanges: LiveData<SPStickerItem?>
        get() = _selectStickerChanges

    override fun onSelectStickerItem(item: SPStickerItem?) {
        _selectStickerChanges.postValue(item)
    }
}
