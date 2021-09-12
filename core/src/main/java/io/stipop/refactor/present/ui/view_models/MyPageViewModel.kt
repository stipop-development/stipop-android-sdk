package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.data.models.SPMyPageMode
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.MyActivePackageRepository
import io.stipop.refactor.domain.repositories.MyHiddenPackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject

class MyPageViewModelV1 @Inject constructor(
    private val _userRepository: UserRepository,
    private val _myActivePackageRepository: MyActivePackageRepository,
    private val _myHiddenPackageRepository: MyHiddenPackageRepository,
) : MyPageViewModel {

    override val user: LiveData<SPUser>
        get() = _userRepository.userChanges

    private val _myPageMode: MutableLiveData<SPMyPageMode> = MutableLiveData<SPMyPageMode>().apply {
        postValue(SPMyPageMode.ACTIVE)
    }
    override val myPageMode: LiveData<SPMyPageMode>
        get() = _myPageMode

    override val myActivePackageList: LiveData<List<SPPackageItem>>
        get() = _myActivePackageRepository.listChanges

    override val myHiddenPackageList: LiveData<List<SPPackageItem>>
        get() = _myHiddenPackageRepository.listChanges

    override fun onLoadMyActivePackageList(index: Int) {
        if (!_myActivePackageRepository.hasLoading) {
            Log.d(
                this::class.simpleName, "onLoadMyActivePackageList : \n " +
                        "index -> $index "
            )
            user.value?.let { user ->
                _myActivePackageRepository.onLoadMoreList(user, "", index)
            }
        }

    }

    override fun onLoadMyHiddenPackageList(index: Int) {
        if (!_myHiddenPackageRepository.hasLoading) {
            Log.d(
                this::class.simpleName, "onLoadMyHiddenPackageList : \n " +
                        "index -> $index "
            )
            user.value?.let { user ->
                _myHiddenPackageRepository.onLoadMoreList(user, "", index)
            }
        }

    }

    override fun onActivePackage(value: SPPackageItem) {
        Log.d(
            this::class.simpleName, "onActivePackage :" +
                    "value.id -> $${value.packageId}"
        )

    }

    override fun onHiddenPackage(value: SPPackageItem) {
        Log.d(
            this::class.simpleName, "onHiddenPackage :" +
                    "value.id -> $${value.packageId}"
        )
    }

    override fun onChangeMyPackageMode(mode: SPMyPageMode) {
        Log.d(
            this::class.simpleName, "onChangeMyPackageMode : " +
                    "mode -> $mode"
        )
        _myPageMode.postValue(mode)
    }
}

interface MyPageViewModel {
    val user: LiveData<SPUser>
    val myPageMode: LiveData<SPMyPageMode>
    val myActivePackageList: LiveData<List<SPPackageItem>>
    val myHiddenPackageList: LiveData<List<SPPackageItem>>
    fun onLoadMyActivePackageList(index: Int)
    fun onLoadMyHiddenPackageList(index: Int)
    fun onActivePackage(value: SPPackageItem)
    fun onHiddenPackage(value: SPPackageItem)
    fun onChangeMyPackageMode(hidden: SPMyPageMode)
}
