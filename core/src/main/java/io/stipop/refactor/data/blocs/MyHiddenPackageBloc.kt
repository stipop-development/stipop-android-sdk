package io.stipop.refactor.data.blocs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.repositories.MyHiddenPackageRepository
import io.stipop.refactor.domain.repositories.StorePackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.MyStickersService
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class MyHiddenPackageBloc {
    abstract val listChanges: LiveData<List<SPPackageItem>>

    abstract fun onLoadMoreList(index: Int)
    abstract fun onActivePackageItem(item: SPPackageItem)
}

class MyHiddenPackageBlocV1
@Inject
constructor(
    private val userRepository: UserRepository,
    private val myStickersService: MyStickersService,
    private val myHiddenPackageRepository: MyHiddenPackageRepository
) : MyHiddenPackageBloc() {


    override val listChanges: LiveData<List<SPPackageItem>>
        get() = myHiddenPackageRepository.listChanges

    override fun onLoadMoreList(index: Int) {
        userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(
                        this@MyHiddenPackageBlocV1::class.simpleName, "[REQ] onLoadMoreList : \n" +
                                "index -> $index"
                    )

                    myHiddenPackageRepository.onLoadMoreList(user, "", index)

                } catch (e: Exception) {
                    Log.e(this@MyHiddenPackageBlocV1::class.simpleName, e.message, e)
                }
            }

        }
    }

    override fun onActivePackageItem(item: SPPackageItem) {
        userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(
                        this@MyHiddenPackageBlocV1::class.simpleName, "[REQ] onActivePackageItem : \n" +
                                "item -> $item"
                    )

                    myStickersService
                        .hideRecoverMyPack(user.apikey, user.userId, item.packageId)
                    .let {
                        Log.d(
                            this@MyHiddenPackageBlocV1::class.simpleName, "[SUCCEED] onActivePackageItem : \n" +
                                    "item -> $item"
                        )
                        myHiddenPackageRepository.onDeleteItem(item)
                    }
                } catch (e: Exception) {
                    Log.e(this@MyHiddenPackageBlocV1::class.simpleName, e.message, e)
                }
            }

        }

    }


}
