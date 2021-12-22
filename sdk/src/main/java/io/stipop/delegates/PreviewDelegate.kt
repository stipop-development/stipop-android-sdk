package io.stipop.delegates

import io.stipop.models.SPSticker

internal interface PreviewDelegate {
    fun onPreviewFavoriteChanged(sticker: SPSticker)
    fun onPreviewStickerClicked(sticker: SPSticker)
}