package io.stipop.refactor.data.blocs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.repositories.StorePackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class StorePackageBloc {

    companion object {
        val TAG: String? = this::class.simpleName
    }

    abstract val packageItemListChanges: LiveData<List<SPPackageItem>>
    abstract val downloadPackageItemChanges: LiveData<SPPackageItem>

    abstract fun onLoadStorePackageItemList(index: Int)
    abstract fun onDownloadPackageItem(item: SPPackageItem)
}


class StorePackageBlocV1
@Inject
constructor(
    private val userRepository: UserRepository,
    private val storePackageRepository: StorePackageRepository,
    private val stickerStoreService: StickerStoreService
) : StorePackageBloc() {

    override val packageItemListChanges: LiveData<List<SPPackageItem>>
        get() = storePackageRepository.listChanges

    private val _downloadPackageItemChanged: MutableLiveData<SPPackageItem> = MutableLiveData()
    override val downloadPackageItemChanges: LiveData<SPPackageItem>
        get() = _downloadPackageItemChanged

    override fun onLoadStorePackageItemList(index: Int) {
        userRepository.currentUser?.let { user ->

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(
                        TAG, "[REG] onLoadStorePackageItemList : \n" +
                                "index -> $index\n"
                    )

                    storePackageRepository.onLoadMoreList(user, "", index)

                    Log.d(
                        TAG, "[SUCCEED] onLoadStorePackageItemList : \n" +
                                "index -> $index\n"
                    )

                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
            }
        }
    }

    override fun onDownloadPackageItem(item: SPPackageItem) {
        userRepository.currentUser?.let {
            user ->

            CoroutineScope(Dispatchers.IO).launch {

                Log.d(
                    TAG, "[REQ] onDownloadPackageItem : \n" +
                            "item -> $item\n"
                )

                stickerStoreService.downloadPurchaseSticker(user.apikey, item.packageId, user.userId, "N", user.language, user.country, null)
                stickerStoreService.stickerPackInfo(user.apikey, item.packageId, user.userId).let {
                    Log.d(
                        TAG, "[RELOAD] onDownloadPackageItem : \n" +
                                "item -> $item\n"
                    )
                    storePackageRepository.onReplaceItem(it.body.packageItem)
                }

            }
        }
    }

}
