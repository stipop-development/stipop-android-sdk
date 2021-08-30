package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.MyStickersDatasource
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.entities.PageMap
import io.stipop.refactor.domain.entities.VoidResponse
import io.stipop.refactor.domain.repositories.MyStickersRepositoryProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class MyStickersRepository @Inject constructor(
    private val remoteDatasource: MyStickersDatasource,
) : MyStickersRepositoryProtocol, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val _activePackageListChanges: BehaviorSubject<List<SPPackage>> =
        BehaviorSubject.create<List<SPPackage>?>().apply {
            onNext(listOf())
        }
    private val _hiddenPackageListChanges: BehaviorSubject<List<SPPackage>> =
        BehaviorSubject.create<List<SPPackage>?>().apply {
            onNext(listOf())
        }

    private val _activePackageList: ArrayList<SPPackage> = arrayListOf()
    private val _hiddenPackageList: ArrayList<SPPackage> = arrayListOf()

    private var _activePackagePageMap: PageMap? = null
    private var _hiddenPackagePageMap: PageMap? = null

    val activePackageList: Observable<List<SPPackage>> =
        Observable.combineLatest(_activePackageListChanges, _hiddenPackageListChanges) { a, b ->
            _activePackageList.apply {
                a.forEach {
                    if (contains(it)) {
                        this[indexOf(it)] = it
                    } else {
                        add(it)
                    }
                }
                b.forEach { remove(it) }
            }
        }
    val hiddenPackageList: Observable<List<SPPackage>> =
        Observable.combineLatest(_hiddenPackageListChanges, _activePackageListChanges) { a, b ->
            _hiddenPackageList.apply {
                a.forEach {
                    if (contains(it)) {
                        this[indexOf(it)] = it
                    } else {
                        add(it)
                    }
                }
                b.forEach { remove(it) }
            }
        }

    suspend fun onActivePackage(apikey: String, userId: String, value: SPPackage) {
        Log.d(
            this::class.simpleName, "onActivePackage : " +
                    "value.id -> ${value.packageId}"
        )

        coroutineScope {
            launch {
                hideRecoverMyPack(apikey, userId, value.packageId)
            }

            _hiddenPackagePageMap?.let {
                if (_hiddenPackageList.size - 1 < it.pageNumber * it.onePageCountRow) {
                    hiddenStickerPacks(apikey, userId, pageNumber = (_hiddenPackageList.size - 1) / it.onePageCountRow + 1)
                }
            }
            _activePackageListChanges.onNext(listOf(value))
        }
    }

    suspend fun onHiddenPackage(apikey: String, userId: String, value: SPPackage) {
        Log.d(
            this::class.simpleName, "onHiddenPackage : " +
                    "value.id -> ${value.packageId}"
        )

        coroutineScope {
            launch {
                hideRecoverMyPack(apikey, userId, value.packageId)
            }

            _activePackagePageMap?.let {
                if (_activePackageList.size - 1 < it.pageNumber * it.onePageCountRow) {
                    myStickerPacks(apikey, userId, pageNumber = (_activePackageList.size - 1) / it.onePageCountRow + 1)
                }
            }

            _hiddenPackageListChanges.onNext(listOf(value))
        }
    }

    override suspend fun myStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): PackageListResponse {
        return remoteDatasource.myStickerPacks(apikey, userId, limit, pageNumber).apply {
            _activePackagePageMap = body.pageMap
            body.packageList?.let {
                _activePackageListChanges.onNext(it.map { SPPackage.fromEntity(it) })
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
            _hiddenPackagePageMap = body.pageMap
            body.packageList?.let {
                _hiddenPackageListChanges.onNext(it.map { SPPackage.fromEntity(it) })
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
