package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.StickerStoreRestDatasource
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.PackageResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPVoidResponse
import io.stipop.refactor.domain.repositories.StickerStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StickerStoreDataRepository @Inject constructor(
    private val remoteDatasource: StickerStoreRestDatasource,
) : StickerStoreRepository {
    private var _hasMoreAllPackageList: Boolean = false
    private var _allPackagePageMap: SPPageMap? = null
    private val _allPackageList: ArrayList<SPPackage> = arrayListOf()
    private val _allPackageListChanges: BehaviorSubject<List<SPPackage>> =
        BehaviorSubject.create<List<SPPackage>?>().apply {
            onNext(listOf())
        }

    override val allPackageList: Observable<List<SPPackage>> = _allPackageListChanges.map { list ->
        list.forEach {
            if (_allPackageList.contains(it)) {
                _allPackageList[_allPackageList.indexOf(it)] = it
            } else {
                _allPackageList.add(it)
            }
        }

        _allPackageList
    }

    private var _hasMoreSearchPackageList: Boolean = false
    private var _searchPackagePageMap: SPPageMap? = null
    private val _searchPackageList: ArrayList<SPPackage> = arrayListOf()
    private val _searchPackageListChanges: BehaviorSubject<List<SPPackage>> =
        BehaviorSubject.create<List<SPPackage>?>().apply {
            onNext(listOf())
        }

    override val searchPackageList: Observable<List<SPPackage>> = _searchPackageListChanges.map { list ->
        list.forEach {
            if (_searchPackageList.contains(it)) {
                _searchPackageList[_searchPackageList.indexOf(it)] = it
            } else {
                _searchPackageList.add(it)
            }
        }

        _searchPackageList
    }

    override suspend fun trendingStickerPacks(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        premium: String?,
        limit: Int?,
        pageNumber: Int?,
        animated: String?
    ): SPPackageListResponse {

        return remoteDatasource.trendingStickerPacks(
            apikey,
            q,
            userId,
            lang,
            countryCode,
            premium,
            limit,
            pageNumber,
            animated
        )
    }

    override suspend fun stickerPackInfo(apikey: String, packId: Int, userId: String): PackageResponse {
        return remoteDatasource.stickerPackInfo(apikey, packId, userId)
    }

    override suspend fun downloadPurchaseSticker(
        apikey: String,
        packId: Int,
        userId: String,
        isPurchase: String,
        lang: String?,
        countryCode: String?,
        price: String?
    ): SPVoidResponse {
        return remoteDatasource.downloadPurchaseSticker(apikey, packId, userId, isPurchase, lang, countryCode, price)
    }

    override fun onLoadAllPackageList(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        premium: String?,
        limit: Int?,
        pageNumber: Int?,
        animated: String?,
    ) {
        Log.d(
            this::class.simpleName, "onLoadAllPackageList : \n" +
                    "apikey -> $apikey\n" +
                    "q -> $q\n" +
                    "userId -> $userId\n" +
                    "lang -> $lang\n" +
                    "countryCode -> $countryCode\n" +
                    "premium -> $premium\n" +
                    "limit -> $limit\n" +
                    "pageNumber -> $pageNumber\n" +
                    "animated -> $animated\n" +
                    ""
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                trendingStickerPacks(apikey, q, userId, lang, countryCode, premium, limit, pageNumber, animated)
            response.body.let { _body ->
                _allPackagePageMap = _body.pageMap
                _body.packageList?.let { list ->
                    _allPackageListChanges.onNext(list.map { SPPackage.fromEntity(it) })
                }
            }
        }
    }

    override fun onLoadSearchPackageList(
        apikey: String,
        userId: String,
        keyword: String,
        lastIndex: Int?,
        lang: String?,
        countryCode: String?,
        premium: String?,
        limit: Int?,
        pageNumber: Int?,
        animated: String?,
    ) {
        Log.d(
            this::class.simpleName, "onLoadSearchPackageList : \n" +
                    "apikey -> $apikey\n" +
                    "userId -> $userId\n" +
                    "keyword -> $keyword\n" +
                    "lastIndex -> $lastIndex\n" +
                    "lang -> $lang\n" +
                    "countryCode -> $countryCode\n" +
                    "premium -> $premium\n" +
                    "limit -> $limit\n" +
                    "pageNumber -> $pageNumber\n" +
                    "animated -> $animated\n" +
                    ""
        )

        CoroutineScope(Dispatchers.IO).launch {

            val response =
                trendingStickerPacks(apikey, keyword, userId, lang, countryCode, premium, limit, pageNumber, animated)
            response.body.let { _body ->
                _searchPackagePageMap = _body.pageMap
                _body.packageList?.let { list ->
                    _searchPackageListChanges.onNext(list.map { SPPackage.fromEntity(it) })
                }
            }
        }
    }

    override fun onDownloadPackage(
        apikey: String,
        userId: String,
        pack: SPPackage,
        isPurchase: String,
        lang: String?,
        countryCode: String?,
        price: String?,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            downloadPurchaseSticker(apikey, pack.packageId, userId, isPurchase, lang, countryCode, price).apply {
                onLoadPackage(apikey, userId, pack)
            }
        }
    }

    suspend fun onLoadPackage(apikey: String, userId: String, pack: SPPackage) {
        stickerPackInfo(apikey, pack.packageId, userId).apply {
            body.let {
                _allPackageListChanges.onNext(listOf(SPPackage.fromEntity(it._package)))
                _searchPackageListChanges.onNext(listOf(SPPackage.fromEntity(it._package)))
            }
        }
    }
}