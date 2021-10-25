package io.stipop.viewholder.delegates

import io.stipop.models.StickerPackage

interface StickerPackageClickDelegate {
    fun onPackageDetailClicked(packageId: Int, entrancePoint: String)
    fun onDownloadClicked(position: Int, stickerPackage: StickerPackage)
}