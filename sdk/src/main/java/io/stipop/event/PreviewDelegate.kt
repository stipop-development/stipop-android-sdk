package io.stipop.event

import io.stipop.models.SPSticker

internal interface PreviewDelegate {
    fun onPreviewFavoriteChanged(sticker: SPSticker)
    fun onPreviewStickerClicked(sticker: SPSticker)
}