package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.repositories.StoreAllPackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject

interface StoreSearchPackageViewModel {
    val listChanges: LiveData<List<SPPackageItem>>
    fun onLoadMore(keyword: String? = null, index: Int)
}

class StoreSearchPackageViewModelV1
@Inject constructor(
    private val _userRepository: UserRepository,
    private val _storeAllPackageRepository: StoreAllPackageRepository,
) : StoreSearchPackageViewModel {

    private var _keyword: String? = null

    override val listChanges: LiveData<List<SPPackageItem>>
        get() = _storeAllPackageRepository.listChanges

    override fun onLoadMore(keyword: String?, index: Int) {
        _keyword = keyword
        _userRepository.currentUser?.let { user ->
            _storeAllPackageRepository.onLoadMoreList(user, _keyword ?: "", index)
        }
    }

}
