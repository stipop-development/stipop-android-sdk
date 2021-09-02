package io.stipop.refactor.domain.repositories.sticker_send

import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.PagingRepository

interface RecentlySentStickersRepository: PagingRepository<SPStickerItem> {
}
