package io.stipop.refactor.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.data.datasources.MyStickersDatasource
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.VoidResponse
import io.stipop.refactor.domain.repositories.MyStickersRepositoryProtocol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyStickersRepository @Inject constructor(
    private val remoteDatasource: MyStickersDatasource,
) : MyStickersRepositoryProtocol {
    private val _activePackageListChanges: MutableLiveData<List<SPPackage>> = MutableLiveData()
    private val _hiddenPackageListChanges: MutableLiveData<List<SPPackage>> = MutableLiveData()

    private val _activePackageList: MediatorLiveData<List<SPPackage>> =
        MediatorLiveData<List<SPPackage>>().apply {
            addSource(_activePackageListChanges) {

                    sourceList ->

                val _arrayList = arrayListOf<SPPackage>()

                value?.let {
                    _arrayList.addAll(it)
                }

                sourceList.forEach { source ->
                    if (_arrayList.contains(source)) {
                        _arrayList[_arrayList.indexOf(source)] = source
                    } else {
                        _arrayList.add(source)
                    }
                }

                postValue(_arrayList)

            }
            addSource(_hiddenPackageListChanges) {

                    sourceList ->

                val _arrayList = arrayListOf<SPPackage>()

                value?.let {
                    _arrayList.addAll(it)
                }

                sourceList.forEach { source ->
                    _arrayList.remove(source)
                }

                postValue(_arrayList)

            }
        }

    private val _hiddenPackageList: MediatorLiveData<List<SPPackage>> =
        MediatorLiveData<List<SPPackage>>().apply {
            addSource(_activePackageListChanges) {

                    sourceList ->

                val _arrayList = arrayListOf<SPPackage>()

                value?.let {
                    _arrayList.addAll(it)
                }

                sourceList.forEach { source ->
                    _arrayList.remove(source)
                }

                postValue(_arrayList)

            }
            addSource(_hiddenPackageListChanges) {

                    sourceList ->

                val arrayList = arrayListOf<SPPackage>()

                value?.let {
                    arrayList.addAll(it)
                }

                sourceList.forEach { source ->
                    if (arrayList.contains(source)) {
                        arrayList[arrayList.indexOf(source)] = source
                    } else {
                        arrayList.add(source)
                    }
                }

                postValue(arrayList)

            }
        }

    val activePackageList: LiveData<List<SPPackage>> get() = _activePackageList
    val hiddenPackageList: LiveData<List<SPPackage>> get() = _hiddenPackageList

    suspend fun onActivePackage(apikey: String, userId: String, value: SPPackage) {
        Log.d(
            this::class.simpleName, "onActivePackage : " +
                    "value.id -> ${value.packageId}"
        )
        hideRecoverMyPack(apikey, userId, value.packageId).apply {
            _activePackageListChanges.postValue(listOf(value))
        }
    }

    suspend fun onHiddenPackage(apikey: String, userId: String, value: SPPackage) {
        Log.d(
            this::class.simpleName, "onHiddenPackage : " +
                    "value.id -> ${value.packageId}"
        )
        hideRecoverMyPack(apikey, userId, value.packageId).apply {
            _hiddenPackageListChanges.postValue(listOf(value))
        }
    }

    override suspend fun myStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): PackageListResponse {
        return remoteDatasource.myStickerPacks(apikey, userId, limit, pageNumber).apply {
            body.packageList?.let {
                _activePackageListChanges.postValue(it.map { SPPackage.fromEntity(it) })
            }
        }
    }

    override suspend fun hideRecoverMyPack(apikey: String, userId: String, packId: Int): VoidResponse {
        return remoteDatasource.hideRecoverMyPack(apikey, userId, packId)
    }

    override suspend fun hiddenStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): PackageListResponse {
        return remoteDatasource.hiddenStickerPacks(apikey, userId, limit, pageNumber).apply {
            body.packageList?.let {
                _hiddenPackageListChanges.postValue(it.map { SPPackage.fromEntity(it) })
            }
        }
    }

    override suspend fun myStickerOrder(
        apikey: String,
        userId: String,
        currentOrder: Int,
        newOrder: Int
    ): VoidResponse {
        return remoteDatasource.myStickerOrder(apikey, userId, currentOrder, newOrder)
    }
}
