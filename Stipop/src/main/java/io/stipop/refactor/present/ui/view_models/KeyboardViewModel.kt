package io.stipop.refactor.present.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.MyActiveStickersRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject

class StickerKeyboardViewModelV1
@Inject constructor(
    private val _userRepository: UserRepository,
    private val _myActiveStickersRepository: MyActiveStickersRepository,
) : KeyboardViewModel {

    override val packageList: LiveData<List<SPPackageItem>>
        get() = _myActiveStickersRepository.listChanges.toFlowable(BackpressureStrategy.LATEST).toLiveData()
    override val stickerList: LiveData<List<SPStickerItem>>
        get() = TODO("Not yet implemented")

    override fun onLoadMorePackageList(index: Int) {
        _userRepository.currentUser?.let { user ->
            _myActiveStickersRepository.onLoadMoreList(user, "", index)
        }

    }

    override fun onLoadMoreStickerList(index: Int) {
        _userRepository.currentUser?.let { user ->
            _myActiveStickersRepository.onLoadMoreList(user, "", index)
        }
    }


}

interface KeyboardViewModel {

    val packageList: LiveData<List<SPPackageItem>>
    val stickerList: LiveData<List<SPStickerItem>>

    fun onLoadMorePackageList(index: Int)
    fun onLoadMoreStickerList(index: Int)

}
