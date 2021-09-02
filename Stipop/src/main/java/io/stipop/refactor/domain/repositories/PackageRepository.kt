package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.SPPackageItem

interface PackageRepository:  PagingRepository<SPPackageItem> {
}
