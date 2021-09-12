package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.repositories.StoreAllPackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject

interface StoreAllPackageViewModel {
    val listChanges: LiveData<List<SPPackageItem>>
    fun onLoadMore(index: Int)
}

class StoreAllPackageViewModelV1
@Inject constructor(
    private val _userRepository: UserRepository,
    private val _storeAllPackageRepository: StoreAllPackageRepository,
) : StoreAllPackageViewModel {

    override val listChanges: LiveData<List<SPPackageItem>>
        get() = _storeAllPackageRepository.listChanges

    override fun onLoadMore(index: Int) {
        _userRepository.currentUser?.let {
            user ->
            _storeAllPackageRepository.onLoadMoreList(user, "", index)
        }
    }

}
