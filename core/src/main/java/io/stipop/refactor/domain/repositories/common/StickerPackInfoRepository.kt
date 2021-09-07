package io.stipop.refactor.domain.repositories.common

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser

interface StickerPackInfoRepository {

    val packageItem: SPPackageItem?
    val packageItemChanges: Observable<SPPackageItem>

    fun onLoad(user: SPUser, packId: Int)
}
