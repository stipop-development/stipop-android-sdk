package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.StoreAllPackageRepository
import io.stipop.refactor.domain.repositories.StoreSearchPackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class StorePageViewModelV1 @Inject constructor(
    private val _userRepository: UserRepository,
    private val _storeAllPackageRepository: StoreAllPackageRepository,
    private val _storeSearchPackageRepository: StoreSearchPackageRepository,
) : StorePageViewModel {

    override val user: LiveData<SPUser>
        get() = _userRepository.userChanges

    private val _storePageMode: MutableLiveData<StorePageMode> = MutableLiveData<StorePageMode>().apply {
        postValue(StorePageMode.ALL)
    }
    override val storePageMode: LiveData<StorePageMode>
        get() = _storePageMode

    private val _storeAllPackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    override val storeAllPackageList: LiveData<List<SPPackage>>
        get() = _storeAllPackageList

    private val _storeSearchPackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    override val storeSearchPackageList: LiveData<List<SPPackage>>
        get() = _storeSearchPackageList

    override fun onChangeStorePageMode(mode: StorePageMode) {
        Log.d(
            this::class.simpleName, "onChangeStorePageMode : " +
                    "mode -> $mode"
        )
        when (mode) {
            StorePageMode.ALL -> {
                onChangeSearchKeyword(null)
            }
            StorePageMode.SEARCH -> {
                onChangeSearchKeyword("")
            }
        }
        _storePageMode.postValue(mode)
    }

    override fun onChangeSearchKeyword(keyword: String?) {
        Log.d(
            this::class.simpleName, "onChangeSearchKeyword : " +
                    "keyword -> $keyword"
        )
        onLoadStoreSearchPackageList(keyword, -1)
    }

    override fun onLoadAllPackageList(lastIndex: Int) {
        _userRepository.currentUser?.let { user ->
            Log.d(this::class.simpleName, "onLoadAllPackageList")
            _storeAllPackageRepository.onLoadMoreList(user, "", lastIndex)
        }
    }

    override fun onLoadStoreSearchPackageList(keyword: String?, lastIndex: Int) {
        _userRepository.currentUser?.let { user ->
            keyword?.let { keyword ->
                Log.d(this::class.simpleName, "onLoadSearchPackageList")
                _storeSearchPackageRepository.onLoadMoreList(user, keyword, lastIndex)
            }
        }
    }

    override fun onDownload(item: SPPackage) {
        Log.d(
            this::class.simpleName, "onDownload : " +
                    "item.packageId -> ${item.packageId}"
        )
        _userRepository.currentUser?.let { user ->
            Log.d(this::class.simpleName, "onLoadSearchPackageList")
//            _storeAllPackageRepository.onDownloadPackage(user.apikey, user.userId, item)
        }
    }
}

interface StorePageViewModel {
    val user: LiveData<SPUser>
    val storePageMode: LiveData<StorePageMode>
    val storeAllPackageList: LiveData<List<SPPackage>>
    val storeSearchPackageList: LiveData<List<SPPackage>>

    fun onChangeStorePageMode(mode: StorePageMode)
    fun onChangeSearchKeyword(keyword: String?)
    fun onLoadAllPackageList(lastIndex: Int)
    fun onLoadStoreSearchPackageList(keyword: String? = "", lastIndex: Int)
    fun onDownload(item: SPPackage)
}

enum class StorePageMode {
    ALL,
    SEARCH,
}
