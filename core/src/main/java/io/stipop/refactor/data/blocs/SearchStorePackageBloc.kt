package io.stipop.refactor.data.blocs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.repositories.StoreSearchPackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class SearchStorePackageBloc {

    companion object {
        val TAG: String? = this::class.simpleName
    }

    abstract val keywordChanges: LiveData<String>
    abstract val packageItemListChanges: LiveData<List<SPPackageItem>>

    abstract fun onChangeKeyword(keyword: String)
    abstract fun onSearchStorePackageItemList(keyword: String?, index: Int)
    abstract fun onDownloadPackageItem(item: SPPackageItem)
}


class SearchStorePackageBlocV1
@Inject
constructor(
    private val userRepository: UserRepository,
    private val storeSearchPackageRepository: StoreSearchPackageRepository,
    private val stickerStoreService: StickerStoreService
) : SearchStorePackageBloc() {

    private val _keywordChanged: MutableLiveData<String> = MutableLiveData()
    override val keywordChanges: LiveData<String>
        get() = _keywordChanged

    override val packageItemListChanges: LiveData<List<SPPackageItem>>
        get() = storeSearchPackageRepository.listChanges

    override fun onChangeKeyword(keyword: String) {
        userRepository.currentUser?.let { user ->

            CoroutineScope(Dispatchers.IO).launch {


                try {
                    Log.d(
                        TAG, "[REG] onChangeKeyword : \n" +
                                "keyword -> $keyword"
                    )
                    _keywordChanged.postValue(keyword)
                    onSearchStorePackageItemList(keyword, -1)

                    Log.d(
                        TAG, "[SUCCEED] onChangeKeyword : \n" +
                                "keyword -> $keyword"
                    )


                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
            }
        }
    }

    override fun onSearchStorePackageItemList(keyword: String?, index: Int) {
        userRepository.currentUser?.let { user ->

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(
                        TAG, "[REG] onSearchStorePackageList : \n" +
                                "keyword -> $keyword \n" +
                                "index -> $index\n"
                    )

                    storeSearchPackageRepository.onLoadMoreList(user, keyword ?: _keywordChanged.value ?: "", index)

                    Log.d(
                        TAG, "[SUCCEED] onSearchStorePackageList : \n" +
                                "keyword -> $keyword \n" +
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

                stickerStoreService.stickerPackInfo(user.apikey, item.packageId, user.userId).run {
                    Log.d(
                        TAG, "[SUCCEED] onDownloadPackageItem : \n" +
                                "item -> $item\n"
                    )
                    packageItemListChanges.value?.indexOf(item)?.let {
                        onSearchStorePackageItemList(null, it)
                    }
                }
            }
        }
    }

}
