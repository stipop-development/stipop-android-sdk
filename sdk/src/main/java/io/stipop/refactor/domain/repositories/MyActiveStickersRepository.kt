package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser

interface MyActiveStickersRepository: PagingRepository<SPPackageItem> {

    fun onHiddenPackage(user: SPUser, index: Int)
}
