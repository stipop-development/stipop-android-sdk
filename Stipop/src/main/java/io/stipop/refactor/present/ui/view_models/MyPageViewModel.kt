package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.stipop.refactor.data.models.SPMyPageMode
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPUser
import io.stipop.refactor.data.repositories.MyStickersRepository
import io.stipop.refactor.data.repositories.UserRepository
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

    private val _myPageMode: MutableLiveData<SPMyPageMode> = MutableLiveData<SPMyPageMode>().apply {
        postValue(SPMyPageMode.ACTIVE)
    }

    private val _user: SPUser? get() = _userRepository.userChanges.value

    override val myPageMode: LiveData<SPMyPageMode>
        get() = _myPageMode
    override val activePackageList: LiveData<List<SPPackage>>
        get() = _myStickersRepository.activePackageList.toFlowable(BackpressureStrategy.LATEST).toLiveData()
    override val hiddenPackageList: LiveData<List<SPPackage>>
        get() = _myStickersRepository.hiddenPackageList.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override fun onLoadMyActivePackageList() {
        if (!_hasLoadingMyActivePackageList) {
            Log.d(this::class.simpleName, "onLoadMyActivePackageList")
            _hasLoadingMyActivePackageList = true
            _user?.let {

                    user ->
                launch {
                    _myStickersRepository.onLoadActivePackageList(
                        user.apikey,
                        user.userId,
                    )
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
                    _myStickersRepository.onLoadHiddenPackageList(
                        user.apikey,
                        user.userId,
                    )
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
    fun onLoadMyActivePackageList()
    fun onLoadMyHiddenPackageList()
    fun onActivePackage(value: SPPackage)
    fun onHiddenPackage(value: SPPackage)
    fun onChangeMyPackageMode(hidden: SPMyPageMode)
}
