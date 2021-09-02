package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.*

interface MyActiveStickersRepository: PagingRepository<SPPackageItem> {

    fun onHiddenPackage(user: SPUser, index: Int)
}
