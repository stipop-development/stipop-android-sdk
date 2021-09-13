package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.blocs.StickerSendBloc
import io.stipop.refactor.domain.entities.SPStickerItem
import javax.inject.Inject

interface StipopViewModel {
    val selectStickerChanges: LiveData<SPStickerItem?>
    val sendStickerChanges: LiveData<SPStickerItem>
    fun onSelectStickerItem(item: SPStickerItem?)
    fun onSendStickerItem(item: SPStickerItem)
}

class StipopViewModelV1 @Inject constructor(
   private  val stickerSendBloc: StickerSendBloc
) : StipopViewModel {

    private val _selectStickerChanges: MutableLiveData<SPStickerItem?> = MutableLiveData()
    override val selectStickerChanges: LiveData<SPStickerItem?>
        get() = _selectStickerChanges

    override val sendStickerChanges: LiveData<SPStickerItem>
        get() = stickerSendBloc.stickerChanges

    override fun onSelectStickerItem(item: SPStickerItem?) {
        _selectStickerChanges.postValue(item)
    }

    override fun onSendStickerItem(item: SPStickerItem) {
        stickerSendBloc.onSendStickerItem(item)
    }
}
