package io.stipop.refactor.domain.repositories

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser

abstract class StickerPackInfoRepository {

    abstract val packageItem: SPPackageItem?
    abstract val packageItemChanges: LiveData<SPPackageItem>

    abstract fun onLoad(user: SPUser, packId: Int)
}
