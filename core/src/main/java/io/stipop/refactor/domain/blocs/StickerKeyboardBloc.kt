package io.stipop.refactor.domain.blocs

import androidx.lifecycle.LiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem

abstract class StickerKeyboardBloc {

    abstract val listChanges: LiveData<List<SPStickerItem>>

    abstract fun getStickerList(packageItem: SPPackageItem?, index: Int)
}
