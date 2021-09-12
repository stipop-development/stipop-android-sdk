package io.stipop.refactor.domain.blocs

import androidx.lifecycle.LiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem

abstract class StickerKeyboardBloc {

    abstract val packageItemListChanges: LiveData<List<SPPackageItem>>
    abstract val stickerItemListChanges: LiveData<List<SPStickerItem>>

    abstract fun onLoadMoreStickerItemList(packageItem: SPPackageItem?, index: Int)
    abstract fun onLoadMorePackageItemList(index: Int)
}

abstract class StickerSendBloc {

    abstract val stickerChanges: LiveData<SPStickerItem>

    abstract fun onSelectStickerItem(id: Int)
}
