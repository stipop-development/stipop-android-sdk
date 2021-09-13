package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.data.blocs.MyActivePackageBloc
import io.stipop.refactor.data.blocs.MyHiddenPackageBloc
import io.stipop.refactor.data.models.SPMyPageMode
import io.stipop.refactor.domain.entities.SPPackageItem
import javax.inject.Inject

class MyPageViewModelV1 @Inject constructor(
    private val myActivePackageBloc: MyActivePackageBloc,
    private val myHiddenPackageBloc: MyHiddenPackageBloc,
) : MyPageViewModel {

    private val _myPageMode: MutableLiveData<SPMyPageMode> = MutableLiveData<SPMyPageMode>().apply {
        postValue(SPMyPageMode.ACTIVE)
    }
    override val myPageMode: LiveData<SPMyPageMode>
        get() = _myPageMode

    override val myActivePackageListChanges: LiveData<List<SPPackageItem>>
        get() = myActivePackageBloc.listChanges

    override val myHiddenPackageListChanges: LiveData<List<SPPackageItem>>
        get() = myHiddenPackageBloc.listChanges

    override fun onLoadMyActivePackageList(index: Int) {
        myActivePackageBloc.onLoadMoreList(index)
    }

    override fun onLoadMyHiddenPackageList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadMyHiddenPackageList :" +
                    "index -> $$index")
        myHiddenPackageBloc.onLoadMoreList(index)
    }

    override fun onActivePackageItem(item: SPPackageItem) {
        Log.d(
            this::class.simpleName, "onActivePackageItem :" +
                    "item -> $$item"
        )
        myHiddenPackageBloc.onActivePackageItem(item)
    }

    override fun onHiddenPackageItem(item: SPPackageItem) {
        Log.d(
            this::class.simpleName, "onHiddenPackageItem :" +
                    "item -> $$item"
        )
        myActivePackageBloc.onHiddenPackageItem(item)
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
    val myPageMode: LiveData<SPMyPageMode>
    val myActivePackageListChanges: LiveData<List<SPPackageItem>>
    val myHiddenPackageListChanges: LiveData<List<SPPackageItem>>
    fun onLoadMyActivePackageList(index: Int)
    fun onLoadMyHiddenPackageList(index: Int)
    fun onActivePackageItem(value: SPPackageItem)
    fun onHiddenPackageItem(value: SPPackageItem)
    fun onChangeMyPackageMode(hidden: SPMyPageMode)
}
