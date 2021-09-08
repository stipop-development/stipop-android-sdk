package io.stipop.refactor.domain.repositories.sticker_store

import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.PagingRepository

interface TrendingStickerPacksRepository : PagingRepository<SPPackageItem> {

}
