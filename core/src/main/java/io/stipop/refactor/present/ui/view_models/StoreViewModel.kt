package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.repositories.MyStickersRepository
import io.stipop.refactor.domain.repositories.StickerStoreRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject

class StoreViewModelV1 @Inject constructor(
    val userRepository: UserRepository,
    val stickerStoreRepository: StickerStoreRepository,
    val myStickersRepository: MyStickersRepository,
    ) : StoreViewModel {
    private val _storeMode: MutableLiveData<StoreMode> = MutableLiveData()
    override val storeMode: LiveData<StoreMode>
        get() = _storeMode

    override fun onChangeStoreMode(mode: StoreMode) {
        _storeMode.value.let {
            if (it == null || it != mode) {
                _storeMode.postValue(mode)
            }
        }
    }
}

interface StoreViewModel {

    val storeMode: LiveData<StoreMode>

    fun onChangeStoreMode(mode: StoreMode)

}

enum class StoreMode {
    STORE_PAGE,
    MY_PAGE;

    val rawValue: Int get() = StoreMode.values().indexOf(this)

    companion object {
        const val TAG: String = "StoreMode"
    }
}
