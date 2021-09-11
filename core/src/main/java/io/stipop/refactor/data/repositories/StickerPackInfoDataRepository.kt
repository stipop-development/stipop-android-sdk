package io.stipop.refactor.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.StickerPackInfoRepository
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StickerPackInfoDataRepository @Inject constructor(
    private val service: StickerStoreService
) : StickerPackInfoRepository() {

    val _packageItemChanged: MutableLiveData<SPPackageItem> = MutableLiveData()

    override val packageItem: SPPackageItem?
        get() = _packageItemChanged.value

    override val packageItemChanges: LiveData<SPPackageItem>
        get() = _packageItemChanged

    override fun onLoad(user: SPUser, packId: Int) {
        Log.d(
            this::class.simpleName, "onLoad : \n" +
                    "packId -> $packId \n"
        )
        CoroutineScope(Dispatchers.IO).launch {
            service.stickerPackInfo(user.apikey, packId, user.userId)
                .run {
                    body.packageItem.run {
                        _packageItemChanged.postValue(this)
                    }
                }
        }

    }
}
