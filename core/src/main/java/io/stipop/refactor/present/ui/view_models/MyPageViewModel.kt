package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.stipop.refactor.data.models.SPMyPageMode
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.MyStickersRepository
import io.stipop.refactor.domain.repositories.UserRepository
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

    private val _user: MutableLiveData<SPUser> = MutableLiveData()
    override val user: LiveData<SPUser>
        get() = _user

    override val myPageMode: LiveData<SPMyPageMode>
        get() = _myPageMode
    private val _myActivePackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    override val myActivePackageList: LiveData<List<SPPackage>>
        get() = _myActivePackageList

    private val _myHiddenPackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    override val myHiddenPackageList: LiveData<List<SPPackage>>
        get() = _myHiddenPackageList

    override fun onLoadMyActivePackageList() {
        if (!_hasLoadingMyActivePackageList) {
            Log.d(this::class.simpleName, "onLoadMyActivePackageList")
            _hasLoadingMyActivePackageList = true
            _userRepository.currentUser?.let {

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
            _userRepository.currentUser?.let {

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
        _userRepository.currentUser?.run {
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
        _userRepository.currentUser?.run {
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
    val user: LiveData<SPUser>
    val myPageMode: LiveData<SPMyPageMode>
    val myActivePackageList: LiveData<List<SPPackage>>
    val myHiddenPackageList: LiveData<List<SPPackage>>
    fun onLoadMyActivePackageList()
    fun onLoadMyHiddenPackageList()
    fun onActivePackage(value: SPPackage)
    fun onHiddenPackage(value: SPPackage)
    fun onChangeMyPackageMode(hidden: SPMyPageMode)
}
