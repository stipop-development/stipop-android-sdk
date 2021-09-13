package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import io.stipop.refactor.data.blocs.PackageItemDetailBloc
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPPackageItem
import javax.inject.Inject

class DetailViewModelV1 @Inject constructor(
    private val bloc: PackageItemDetailBloc
) : DetailViewModel {

    override val packageItemChanges: LiveData<SPPackageItem>
        get() = bloc.packageItemChanges

    fun loadPackage(packageId: Int) {
        bloc.onLoadPackageItem(packageId)
    }
}

interface DetailViewModel {
    val packageItemChanges: LiveData<SPPackageItem>
}
