package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.MyStickersDatasource
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.*
import io.stipop.refactor.domain.repositories.MyStickersRepositoryProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyStickersRepository @Inject constructor(
    private val remoteDatasource: MyStickersDatasource,
) : MyStickersRepositoryProtocol {
    private val _disposable = CompositeDisposable()

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

    private var _activePackagePageMap: SPPageMap? = null
    private var _hiddenPackagePageMap: SPPageMap? = null

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

    suspend fun onLoadActivePackageList(
        apikey: String,
        userId: String,
        limit: Int? = null,
        pageNumber: Int? = (_activePackagePageMap?.pageNumber ?: 0) + 1
    ) {
        myStickerPacks(apikey, userId, limit, pageNumber)
    }

    suspend fun onLoadHiddenPackageList(
        apikey: String,
        userId: String,
        limit: Int? = null,
        pageNumber: Int? = (_hiddenPackagePageMap?.pageNumber ?: 0) + 1
    ) {
        hiddenStickerPacks(apikey, userId, limit, pageNumber)
    }

    suspend fun onActivePackage(apikey: String, userId: String, value: SPPackage) {
        Log.d(
            this::class.simpleName, "onActivePackage : " +
                    "value.id -> ${value.packageId}"
        )

        hideRecoverMyPack(apikey, userId, value.packageId)
        _activePackageListChanges.onNext(listOf(value))

        val hiddenPackageListSubscription = hiddenPackageList.subscribe { list ->
            _hiddenPackagePageMap?.let { pageMap ->
                if (list.size < pageMap.pageNumber * pageMap.onePageCountRow) {
                    CoroutineScope(Dispatchers.IO).launch {
                        onLoadHiddenPackageList(
                            apikey,
                            userId,
                            pageNumber = list.size / pageMap.onePageCountRow + 1
                        )
                        _disposable.dispose()
                    }
                }
            }
        }

        _disposable.add(hiddenPackageListSubscription)
    }

    suspend fun onHiddenPackage(apikey: String, userId: String, value: SPPackage) {
        Log.d(
            this::class.simpleName, "onHiddenPackage : " +
                    "value.id -> ${value.packageId}"
        )

        hideRecoverMyPack(apikey, userId, value.packageId)

        _hiddenPackageListChanges.onNext(listOf(value))

        val activePackageListSubscription = activePackageList.subscribe { list ->
            _activePackagePageMap?.let { pageMap ->
                if (list.size < pageMap.pageNumber * pageMap.onePageCountRow) {
                    CoroutineScope(Dispatchers.IO).launch {
                        onLoadActivePackageList(
                            apikey,
                            userId,
                            pageNumber = list.size / pageMap.onePageCountRow + 1
                        )
                    }
                    _disposable.dispose()
                }
            }
        }

        _disposable.add(activePackageListSubscription)
    }

    override suspend fun myStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): SPPackageListResponse {
        try {
            return remoteDatasource.myStickerPacks(apikey, userId, limit, pageNumber).apply {

                SPErrorResponse.fromErrorCode(header.code)?.let {
                    throw it
                }

                _activePackagePageMap = body.pageMap
                body.packageList?.let {
                    _activePackageListChanges.onNext(it.map { SPPackage.fromEntity(it) })
                }
            }
        } catch (e: Exception) {
            Log.e(this::class.simpleName, e.message, e)
            throw e
        }
    }

    override suspend fun hideRecoverMyPack(apikey: String, userId: String, packId: Int): SPVoidResponse {
        return remoteDatasource.hideRecoverMyPack(apikey, userId, packId)
    }

    override suspend fun hiddenStickerPacks(
        apikey: String,
        userId: String,
        limit: Int?,
        pageNumber: Int?
    ): SPPackageListResponse {
        try {
            return remoteDatasource.hiddenStickerPacks(apikey, userId, limit, pageNumber).apply {
                _hiddenPackagePageMap = body.pageMap
                body.packageList?.let {
                    _hiddenPackageListChanges.onNext(it.map { SPPackage.fromEntity(it) })
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun myStickerOrder(
        apikey: String,
        userId: String,
        currentOrder: Int,
        newOrder: Int
    ): SPVoidResponse {
        return remoteDatasource.myStickerOrder(apikey, userId, currentOrder, newOrder)
    }
}
