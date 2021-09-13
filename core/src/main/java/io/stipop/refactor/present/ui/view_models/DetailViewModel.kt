package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import io.stipop.refactor.data.blocs.PackageItemDetailBloc
import io.stipop.refactor.domain.entities.SPPackageItem
import javax.inject.Inject

class DetailViewModelV1 @Inject constructor(
    private val bloc: PackageItemDetailBloc
) : DetailViewModel {

    override val packageItemChanges: LiveData<SPPackageItem>
        get() = bloc.packageItemChanges

    override fun onLoadPackage(packageId: Int) {
        bloc.onLoadPackageItem(packageId)
    }

    override fun onDownloadPackageItem(packageId: Int) {
        bloc.onDownloadPackageItem(packageId)
    }
}

interface DetailViewModel {
    val packageItemChanges: LiveData<SPPackageItem>
    fun onLoadPackage(packageId: Int)
    fun onDownloadPackageItem(packageId: Int)
}
