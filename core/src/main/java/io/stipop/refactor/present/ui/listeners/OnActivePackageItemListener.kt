package io.stipop.refactor.present.ui.listeners

import io.stipop.refactor.domain.entities.SPPackageItem

interface OnActivePackageItemListener {
    fun onActive(item: SPPackageItem)
}
