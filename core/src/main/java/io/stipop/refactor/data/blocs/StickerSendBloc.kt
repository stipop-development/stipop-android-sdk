package io.stipop.refactor.data.blocs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.domain.blocs.StickerSendBloc
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.StickerSendService
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class StickerSendBlocV1
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val stickerSendService: StickerSendService,
        private val stickerStoreService: StickerStoreService
    )
    : StickerSendBloc() {
    private val _stickerChanges: MutableLiveData<SPStickerItem> = MutableLiveData()
    override val stickerChanges: LiveData<SPStickerItem>
        get() = _stickerChanges

    override fun onSendStickerItem(item: SPStickerItem) {
        userRepository.currentUser?.let {
            user ->

            CoroutineScope(Dispatchers.IO).launch {


                try {
                    Log.d(TAG, "[REG] onSendStickerItem : \n" +
                            "item -> $item")
                    stickerSendService.registerStickerSend(user.apikey, item.stickerId, user.userId, user.language, user.country, "")

                    Log.d(TAG, "[SUCCEED] onSendStickerItem : \n" +
                            "item -> $item")

                    _stickerChanges.postValue(item)

                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
            }
        }
    }
}
