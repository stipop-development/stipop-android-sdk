package io.stipop.event

import io.stipop.models.StickerPackage

internal interface StickerPackageClickDelegate {
    fun onPackageDetailClicked(packageId: Int, entrancePoint: String)
    fun onDownloadClicked(position: Int, stickerPackage: StickerPackage)
}