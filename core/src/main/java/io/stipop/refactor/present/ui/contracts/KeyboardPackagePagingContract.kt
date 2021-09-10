package io.stipop.refactor.present.ui.contracts

import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.present.ui.listeners.OnItemSelectListener

interface KeyboardPackagePagingContract {
    interface View : PagingContract.View<SPPackageItem> {
        fun setOnItemSelectListener(listener: OnItemSelectListener<SPPackageItem>)
    }

    interface Presenter : PagingContract.Presenter<SPPackageItem> {

    }
}
