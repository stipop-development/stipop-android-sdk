package io.stipop.refactor.present.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.Config.Companion.apikey
import io.stipop.refactor.data.models.SPMyPageMode
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPUser
import io.stipop.refactor.data.repositories.MyStickersRepository
import io.stipop.refactor.data.repositories.UserRepository
import io.stipop.refactor.domain.entities.PageMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MyPageViewModel @Inject constructor(
    private val _userRepository: UserRepository,
    private val _myStickersRepository: MyStickersRepository,
) : MyPageViewModelProtocol, CoroutineScope {

    private var _hasLoadingMyActivePackageList: Boolean = false
    private var _hasLoadingMyHiddenPackageList: Boolean = false

    private var _hasMoreMyActivePackageList: Boolean = false
    private var _hasMoreMyHiddenPackageList: Boolean = false

    private var _myActivePackageListPageNumber: Int = 0
    private var _myHiddenPackageListPageNumber: Int = 0

    private val _myPageMode: MutableLiveData<SPMyPageMode> = MutableLiveData<SPMyPageMode>().apply {
        postValue(SPMyPageMode.ACTIVE)
    }
    private val _activePackagePageMapChanges: MutableLiveData<PageMap?> = MutableLiveData()
    private val _hiddenPackagePageMapChanges: MutableLiveData<PageMap?> = MutableLiveData()

    private val _activePackagePageMap: MediatorLiveData<PageMap?> = MediatorLiveData<PageMap?>().apply {
        addSource(_activePackagePageMapChanges) {

            Log.d(this@MyPageViewModel::class.simpleName, "$it")
            it?.let {
                _myActivePackageListPageNumber = it.pageNumber
                _hasMoreMyActivePackageList = it.pageCount > it.pageNumber
            }

            postValue(it)
        }
    }
    private val _hiddenPackagePageMap: MediatorLiveData<PageMap?> = MediatorLiveData<PageMap?>().apply {
        addSource(_hiddenPackagePageMapChanges) {

            Log.d(this@MyPageViewModel::class.simpleName, "$it")
            it?.let {
                _myHiddenPackageListPageNumber = it.pageNumber
                _hasMoreMyHiddenPackageList = it.pageCount > it.pageNumber
            }

            postValue(it)
        }
    }

    private val _user: SPUser? get() = _userRepository.userChanges.value

    override val myPageMode: LiveData<SPMyPageMode>
        get() = _myPageMode
    override val activePackageList: LiveData<List<SPPackage>>
        get() = _myStickersRepository.activePackageList
    override val hiddenPackageList: LiveData<List<SPPackage>>
        get() = _myStickersRepository.hiddenPackageList
    override val activePackagePageMap: LiveData<PageMap?>
        get() = _activePackagePageMap
    override val hiddenPackagePageMap: LiveData<PageMap?>
        get() = _hiddenPackagePageMap

    override fun onLoadMyActivePackageList() {
        if (!_hasLoadingMyActivePackageList) {
            Log.d(this::class.simpleName, "onLoadMyActivePackageList")
            _hasLoadingMyActivePackageList = true
            _user?.let {

                    user ->
                launch {
                    _myStickersRepository.myStickerPacks(
                        user.apikey,
                        user.userId,
                        pageNumber = _myActivePackageListPageNumber + 1
                    ).let { response ->
                        try {
                            _myActivePackageListPageNumber = response.body.pageMap.pageNumber
                            _hasMoreMyActivePackageList =
                                response.body.pageMap.pageCount > response.body.pageMap.pageNumber
                        } catch (e: Exception) {
                            Log.e(this@MyPageViewModel::class.simpleName, e.message, e)
                        }
                    }
                    _hasLoadingMyActivePackageList = false
                }
            }
        }
    }

    override fun onLoadMyHiddenPackageList() {
        if (!_hasLoadingMyHiddenPackageList) {
            Log.d(this::class.simpleName, "onLoadMyHiddenPackageList")
            _hasLoadingMyHiddenPackageList = true
            _user?.let {

                user ->

                launch {
                    _myStickersRepository.hiddenStickerPacks(
                        user.apikey,
                        user.userId,
                        pageNumber = _myHiddenPackageListPageNumber + 1
                    ).let {
                        response ->
                        try {
                            _myHiddenPackageListPageNumber = response.body.pageMap.pageNumber
                            _hasMoreMyHiddenPackageList =
                                response.body.pageMap.pageCount > response.body.pageMap.pageNumber
                        } catch (e: Exception) {
                            Log.e(this@MyPageViewModel::class.simpleName, e.message, e)
                        }
                    }
                    _hasLoadingMyHiddenPackageList = false
                }
            }
        }

    }

    override fun onActivePackage(value: SPPackage) {
        Log.d(
            this::class.simpleName, "onActivePackage :" +
                    "value.id -> $${value.packageId}"
        )
        _user?.run {
            launch {
                _myStickersRepository.onActivePackage(apikey, userId, value)
            }
        }
    }

    override fun onHiddenPackage(value: SPPackage) {
        Log.d(
            this::class.simpleName, "onHiddenPackage :" +
                    "value.id -> $${value.packageId}"
        )
        _user?.run {
            launch {
                _myStickersRepository.onHiddenPackage(apikey, userId, value)
            }
        }
    }

    override fun onChangeMyPackageMode(mode: SPMyPageMode) {
        Log.d(
            this::class.simpleName, "onChangeMyPackageMode : " +
                    "mode -> $mode"
        )
        _myPageMode.postValue(mode)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

}

interface MyPageViewModelProtocol {
    val myPageMode: LiveData<SPMyPageMode>
    val activePackageList: LiveData<List<SPPackage>>
    val hiddenPackageList: LiveData<List<SPPackage>>
    val activePackagePageMap: LiveData<PageMap?>
    val hiddenPackagePageMap: LiveData<PageMap?>
    fun onLoadMyActivePackageList()
    fun onLoadMyHiddenPackageList()
    fun onActivePackage(value: SPPackage)
    fun onHiddenPackage(value: SPPackage)
    fun onChangeMyPackageMode(hidden: SPMyPageMode)
}
