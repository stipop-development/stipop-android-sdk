package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import io.stipop.refactor.data.blocs.StorePackageBloc
import io.stipop.refactor.domain.entities.SPPackageItem
import javax.inject.Inject

interface StorePackageViewModel {
    val listChanges: LiveData<List<SPPackageItem>>
    fun onLoadMore(index: Int)
    fun onDownloadPackageItem(item: SPPackageItem)
}

class StorePackageViewModelV1
@Inject constructor(
    private val storePackageBloc: StorePackageBloc,
) : StorePackageViewModel {

    override val listChanges: LiveData<List<SPPackageItem>>
        get() = storePackageBloc.packageItemListChanges

    override fun onLoadMore(index: Int) {
        storePackageBloc.onLoadStorePackageItemList(index)
    }

    override fun onDownloadPackageItem(item: SPPackageItem) {
        storePackageBloc.onDownloadPackageItem(item)
    }
}
