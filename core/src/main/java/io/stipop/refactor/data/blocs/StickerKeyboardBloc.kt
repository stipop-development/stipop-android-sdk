package io.stipop.refactor.data.blocs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.blocs.StickerKeyboardBloc
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
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
    private val _recentlySentStickersRepository: RecentlySentStickersRepository,
    private val _stickerStoreService: StickerStoreService,
) : StickerKeyboardBloc() {

    private var _stickerListChanged : MutableLiveData<List<SPStickerItem>> = MutableLiveData()

    override val listChanges: LiveData<List<SPStickerItem>> = MediatorLiveData<List<SPStickerItem>>().apply {
        addSource(_recentlySentStickersRepository.listChanges) {
            postValue(it)
        }
        addSource(_stickerListChanged) {
            postValue(it)
        }
    }

    override fun getStickerList(packageItem: SPPackageItem?, index: Int) {
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
}
