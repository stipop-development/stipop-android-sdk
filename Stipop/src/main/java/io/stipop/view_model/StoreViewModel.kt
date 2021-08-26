package io.stipop.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StoreViewModel : ViewModel(), StoreViewModelProtocol {
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
