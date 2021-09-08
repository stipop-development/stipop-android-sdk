package io.stipop.refactor.data.repositories.common

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.common.StickerPackInfoRepository
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StickerPackInfoDataRepository @Inject constructor(
    private val service: StickerStoreService
) : StickerPackInfoRepository {

    val _packageItemChanged: BehaviorSubject<SPPackageItem> = BehaviorSubject.create()

    override val packageItem: SPPackageItem?
        get() = _packageItemChanged.value

    override val packageItemChanges: Observable<SPPackageItem>
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
                        _packageItemChanged.onNext(this)
                    }
                }
        }

    }
}
