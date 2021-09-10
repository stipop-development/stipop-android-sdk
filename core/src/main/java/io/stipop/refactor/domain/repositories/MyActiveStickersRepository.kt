package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser

abstract class MyActiveStickersRepository: PagingRepository<SPPackageItem>() {

    abstract fun onHiddenPackage(user: SPUser, index: Int)
}
