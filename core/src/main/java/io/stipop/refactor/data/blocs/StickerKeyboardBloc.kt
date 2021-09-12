package io.stipop.refactor.data.blocs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.blocs.StickerKeyboardBloc
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.MyActivePackageRepository
import io.stipop.refactor.domain.repositories.RecentlySentStickersRepository
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StickerKeyboardBlocV1
@Inject
constructor(
    private val _userRepository: UserRepository,
    private val _myActiveStickersRepository: MyActivePackageRepository,
    private val _recentlySentStickersRepository: RecentlySentStickersRepository,
    private val _stickerStoreService: StickerStoreService,
) : StickerKeyboardBloc() {

    override val packageItemListChanges: LiveData<List<SPPackageItem>>
        get() = _myActiveStickersRepository.listChanges

    private var _stickerListChanged : MutableLiveData<List<SPStickerItem>> = MutableLiveData()
    override val stickerItemListChanges: LiveData<List<SPStickerItem>> = MediatorLiveData<List<SPStickerItem>>().apply {
        addSource(_recentlySentStickersRepository.listChanges) {
            postValue(it)
        }
        addSource(_stickerListChanged) {
            postValue(it)
        }
    }

    override fun onLoadMoreStickerItemList(packageItem: SPPackageItem?, index: Int) {
        _userRepository.currentUser?.let { user ->
            if (packageItem == null) {
                _recentlySentStickersRepository.onLoadMoreList(user, "", index)
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    _stickerStoreService.stickerPackInfo(user.apikey, packageItem.packageId, user.userId).let {
                        response ->
                        response.body.let {
                            body ->
                            body.packageItem.let {
                                packageItem ->
                                _stickerListChanged.postValue(packageItem.stickers)
                            }
                        }
                    }
                }
            }

        }

    }

    override fun onLoadMorePackageItemList(index: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _userRepository.currentUser?.let {
                user ->
                _myActiveStickersRepository.onLoadMoreList(user, "", index)
            }
        }
    }
}
