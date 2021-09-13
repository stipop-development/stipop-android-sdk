package io.stipop.refactor.data.blocs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.StickerStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class PackageItemDetailBloc {
    abstract val packageItemChanges: LiveData<SPPackageItem>

    abstract fun onLoadPackageItem(id: Int)
}

class PackageItemDetailBlocV1
@Inject
constructor(
    private val userRepository: UserRepository,
    private val service: StickerStoreService
) : PackageItemDetailBloc() {


    private val _packageItemChanges: MutableLiveData<SPPackageItem> = MutableLiveData()
    override val packageItemChanges: LiveData<SPPackageItem>
        get() = _packageItemChanges

    override fun onLoadPackageItem(id: Int) {
        userRepository.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    _packageItemChanges.postValue(
                        service.stickerPackInfo(
                            user.apikey,
                            id,
                            user.userId
                        ).body.packageItem
                    )
                } catch (e: Exception) {
                    Log.e(this@PackageItemDetailBlocV1::class.simpleName, e.message, e)
                }
            }

        }
    }


}
