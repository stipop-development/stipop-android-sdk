package io.stipop.refactor.domain.repositories

import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.PagingRepository

abstract class RecentlySentStickersRepository : PagingRepository<SPStickerItem>() {

}
