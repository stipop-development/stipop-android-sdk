package io.stipop.viewholder.delegates

import io.stipop.models.StickerPackage

interface VerticalStickerThumbViewHolderDelegate {
    fun onDownloadClicked(position: Int, stickerPackage: StickerPackage)
}