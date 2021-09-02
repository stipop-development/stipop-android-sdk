package io.stipop.refactor.domain.services

import io.stipop.refactor.domain.entities.SPStickerListResponse
import io.stipop.refactor.domain.entities.SPVoidResponse

interface StickerSendService {
    suspend fun registerStickerSend(
        apikey: String,
        stickerId: Int,
        userId: String,
        lang: String?,
        countryCode: String?,
        q: String?,
    ): SPVoidResponse

    suspend fun recentlySentStickers(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?,
    ): SPStickerListResponse

    suspend fun frequentlySentStickers(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?,
    ): SPStickerListResponse
}
