package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.SPPackageItem

abstract class StoreAllPackageRepository : PagingRepository<SPPackageItem>() {
}
