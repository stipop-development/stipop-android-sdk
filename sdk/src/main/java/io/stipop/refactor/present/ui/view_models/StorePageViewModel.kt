package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.StickerStoreRepository
import io.stipop.refactor.domain.repositories.UserRepository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StorePageViewModel @Inject constructor(
    private val _userRepository: UserRepository,
    private val _stickerStoreRepository: StickerStoreRepository,
) : StorePageViewModelProtocol, CoroutineScope {

    private var _currentJob: Job? = null
    override val user: LiveData<SPUser>
        get() = _userRepository.user.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    private val _storePageMode: MutableLiveData<StorePageMode> = MutableLiveData<StorePageMode>().apply {
        postValue(StorePageMode.ALL)
    }
    override val storePageMode: LiveData<StorePageMode>
        get() = _storePageMode

    override val storeAllPackageList: LiveData<List<SPPackage>>
        get() = _stickerStoreRepository.allPackageList.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override val storeSearchPackageList: LiveData<List<SPPackage>>
        get() = _stickerStoreRepository.searchPackageList.toFlowable(BackpressureStrategy.LATEST).toLiveData()

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
        onLoadStoreSearchPackageList(keyword)
    }

    override fun onLoadAllPackageList(lastIndex: Int?) {
        val supervisor = SupervisorJob()
        if (_currentJob == null || _currentJob?.isCompleted == true) {
            _currentJob = CoroutineScope(Dispatchers.IO + supervisor).launch {
                _userRepository.currentUser?.let { user ->
                    Log.d(this::class.simpleName, "onLoadAllPackageList")
                    _stickerStoreRepository.onLoadAllPackageList(user.apikey, "", user.userId)
                }
            }
        }
    }

    override fun onLoadStoreSearchPackageList(keyword: String?, lastIndex: Int?) {
        if (_currentJob == null || _currentJob?.isCompleted == true) {
            _currentJob = launch {
                _userRepository.currentUser?.let { user ->
                    keyword?.let { keyword ->
                        Log.d(this::class.simpleName, "onLoadSearchPackageList")
                        _stickerStoreRepository.onLoadSearchPackageList(user.apikey, user.userId, keyword, lastIndex)
                    }

                }
            }
        }
    }

    override fun onDownload(item: SPPackage) {
        Log.d(
            this::class.simpleName, "onDownload : " +
                    "item.packageId -> ${item.packageId}"
        )

        if (_currentJob == null || _currentJob?.isCompleted == true) {
            _currentJob = launch {
                _userRepository.currentUser?.let { user ->
                    Log.d(this::class.simpleName, "onLoadSearchPackageList")
                    _stickerStoreRepository.onDownloadPackage(user.apikey, user.userId, item)
                }
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()
}

interface StorePageViewModelProtocol {
    val user: LiveData<SPUser>
    val storePageMode: LiveData<StorePageMode>
    val storeAllPackageList: LiveData<List<SPPackage>>
    val storeSearchPackageList: LiveData<List<SPPackage>>

    fun onChangeStorePageMode(mode: StorePageMode)
    fun onChangeSearchKeyword(keyword: String?)
    fun onLoadAllPackageList(lastIndex: Int? = -1)
    fun onLoadStoreSearchPackageList(keyword: String? = "", lastIndex: Int? = -1)
    fun onDownload(item: SPPackage)
}

enum class StorePageMode {
    ALL,
    SEARCH,
}
