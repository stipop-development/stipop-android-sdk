package io.stipop.refactor.present.ui.contracts

import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.present.ui.listeners.OnItemSelectListener

interface KeyboardStickerPagingContract {
    interface View : PagingContract.View<SPStickerItem> {
        fun setOnItemSelectListener(listener: OnItemSelectListener<SPStickerItem>)
    }

    interface Presenter : PagingContract.Presenter<SPStickerItem> {

    }
}
