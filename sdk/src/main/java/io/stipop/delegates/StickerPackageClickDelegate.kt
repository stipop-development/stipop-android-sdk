package io.stipop.delegates

import io.stipop.models.StickerPackage

internal interface StickerPackageClickDelegate {
    fun onPackageDetailClicked(packageId: Int, entrancePoint: String)
    fun onDownloadClicked(position: Int, stickerPackage: StickerPackage)
}