package io.stipop.refactor.present.ui.listeners

import io.stipop.refactor.data.models.SPPackage

interface OnDownloadPackageListener {
    fun onDownload(item: SPPackage)
}
