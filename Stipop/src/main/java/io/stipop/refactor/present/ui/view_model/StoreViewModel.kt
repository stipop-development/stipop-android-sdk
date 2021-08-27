package io.stipop.refactor.present.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stipop.refactor.data.repositories.MyStickersRepository
import io.stipop.refactor.data.repositories.StickerStoreRepository
import io.stipop.refactor.data.repositories.UserRepository
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    val userRepository: UserRepository,
    val stickerStoreRepository: StickerStoreRepository,
    val myStickersRepository: MyStickersRepository,
    ) : StoreViewModelProtocol {
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

interface StoreViewModelProtocol {

    val storeMode: LiveData<StoreMode>

    fun onChangeStoreMode(mode: StoreMode)

}

enum class StoreMode {
    STORE_PAGE,
    MY_PAGE,
}
