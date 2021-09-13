package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import io.stipop.refactor.data.blocs.SearchStorePackageBloc
import io.stipop.refactor.domain.entities.SPPackageItem
import javax.inject.Inject

interface StoreSearchPackageViewModel {
    val downloadPackageItemChanges: LiveData<SPPackageItem>
    val listChanges: LiveData<List<SPPackageItem>>
    fun onLoadMore(keyword: String? = null, index: Int)
    fun onDownloadPackageItem(it: SPPackageItem)
}

class StoreSearchPackageViewModelV1
@Inject constructor(
    private val searchStorePackageBloc: SearchStorePackageBloc
) : StoreSearchPackageViewModel {

    var _keyword: String? = null

    override val downloadPackageItemChanges: LiveData<SPPackageItem>
        get() = searchStorePackageBloc.downloadPackageItemChanges

    override val listChanges: LiveData<List<SPPackageItem>>
        get() = searchStorePackageBloc.packageItemListChanges

    override fun onLoadMore(keyword: String?, index: Int) {
        searchStorePackageBloc.onSearchStorePackageItemList(_keyword, index)
    }

    override fun onDownloadPackageItem(it: SPPackageItem) {
        searchStorePackageBloc.onDownloadPackageItem(it)
    }

}
