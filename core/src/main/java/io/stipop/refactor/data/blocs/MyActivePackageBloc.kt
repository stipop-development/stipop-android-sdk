package io.stipop.refactor.data.blocs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.repositories.MyActivePackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.MyStickersService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class MyActivePackageBloc {
    abstract val listChanges: LiveData<List<SPPackageItem>>
    abstract fun onLoadMoreList(index: Int)
    abstract fun onMovePackageItem(
        sourceItem: SPPackageItem,
        destItem: SPPackageItem
    )

    abstract fun onHiddenPackageItem(item: SPPackageItem)
}

class MyActivePackageBlocV1
@Inject
constructor(
    private val userRepository: UserRepository,
    private val myStickersService: MyStickersService,
    private val myActivePackageRepository: MyActivePackageRepository
) : MyActivePackageBloc() {

    override val listChanges: LiveData<List<SPPackageItem>> = MediatorLiveData<List<SPPackageItem>>().apply {
        addSource(myActivePackageRepository.listChanges) {
            postValue(it.sortedByDescending { it.order })
        }
    }

    override fun onLoadMoreList(index: Int) {
        userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(
                        this@MyActivePackageBlocV1::class.simpleName, "[REQ] onLoadMoreList : \n" +
                                "index -> $index"
                    )
                    myActivePackageRepository.onLoadMoreList(user, "", index)
                    Log.d(
                        this@MyActivePackageBlocV1::class.simpleName, "[SUCCEED] onLoadMoreList : \n" +
                                "index -> $index"
                    )
                } catch (e: Exception) {
                    Log.e(this@MyActivePackageBlocV1::class.simpleName, e.message, e)
                }
            }

        }
    }

    override fun onMovePackageItem(
        sourceItem: SPPackageItem,
        destItem: SPPackageItem
    ) {
        userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(
                        this@MyActivePackageBlocV1::class.simpleName, "[REQ] onMovePackageItem : \n" +
                                "sourceItem -> $sourceItem \n" +
                                "destItem -> $destItem "
                    )

                    myStickersService.myStickerOrder(
                        user.apikey,
                        user.userId,
                        sourceItem.order,
                        destItem.order,
                    ).let {
                        Log.d(
                            this@MyActivePackageBlocV1::class.simpleName, "[SUCCEED] onMovePackageItem : \n" +
                                    "sourceItem -> $sourceItem \n" +
                                    "destItem -> $destItem "
                        )
                    }

                    listChanges.value?.let {

                        val a = it.indexOf(sourceItem)
                        val b = it.indexOf(destItem)

                        Log.e("TAG", "a = $a")
                        Log.e("TAG", "b = $b")

                        delay(500)
                        myActivePackageRepository.onReloadList(user, "", a)
                    }

                } catch (e: Exception) {
                    Log.e(this@MyActivePackageBlocV1::class.simpleName, e.message, e)
                }
            }

        }

    }

    override fun onHiddenPackageItem(item: SPPackageItem) {
        userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(
                        this@MyActivePackageBlocV1::class.simpleName, "[REQ] onHiddenPackageItem : \n" +
                                "item -> $item \n"
                    )
                    myStickersService.hideRecoverMyPack(
                        user.apikey,
                        user.userId,
                        item.packageId,
                    ).let {
                        Log.d(
                            this@MyActivePackageBlocV1::class.simpleName, "[SUCCEED] onHiddenPackageItem : \n" +
                                    "item -> $item"
                        )
                        myActivePackageRepository.onDeleteItem(item)

                        listChanges.value?.indexOf(item)?.let {
                            if (it >= 0) {
                                myActivePackageRepository.onLoadMoreList(user, "", it)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(this@MyActivePackageBlocV1::class.simpleName, e.message, e)
                }
            }

        }

    }


}
