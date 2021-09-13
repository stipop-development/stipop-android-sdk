package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.data.blocs.SearchStorePackageBloc
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.StoreAllPackageRepository
import io.stipop.refactor.domain.repositories.StoreSearchPackageRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject


interface StorePageViewModel {
    val storePageMode: LiveData<StorePageMode>
    val storeAllPackageList: LiveData<List<SPPackage>>
    val storeSearchPackageList: LiveData<List<SPPackage>>

    fun onChangeStorePageMode(mode: StorePageMode)
    fun onChangeKeyword(keyword: String?)
}

enum class StorePageMode {
    ALL,
    SEARCH,
}

class StorePageViewModelV1 @Inject constructor(
    private val searchStorePackageBloc: SearchStorePackageBloc,
) : StorePageViewModel {

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
                onChangeKeyword(null)
            }
            StorePageMode.SEARCH -> {
                onChangeKeyword("")
            }
        }
        _storePageMode.postValue(mode)
    }

    override fun onChangeKeyword(keyword: String?) {
        Log.d(
            this::class.simpleName, "onChangeSearchKeyword : " +
                    "keyword -> $keyword"
        )
        searchStorePackageBloc.onChangeKeyword(keyword ?: "")
    }
}
