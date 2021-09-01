package io.stipop.refactor.data.repositories

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.SearchRestDatasource
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPKeywordListResponse
import io.stipop.refactor.domain.entities.SPPackageListResponse
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

class SearchDataRepository @Inject constructor(
    private val _remoteDatasource: SearchRestDatasource
) : SearchRepository {
    private var _searchKeywordChanges: BehaviorSubject<String> = BehaviorSubject.create<String>().apply {
        onNext("")
    }
    private var _searchKeywordListChanges: BehaviorSubject<List<SPKeywordItem>> =
        BehaviorSubject.create<List<SPKeywordItem>?>().apply {
            onNext(listOf())
        }
    private var _searchKeywordList: List<SPKeywordItem> = listOf()

    private var _searchStickerListChanges: BehaviorSubject<List<SPStickerItem>> =
        BehaviorSubject.create<List<SPStickerItem>?>().apply {
            onNext(listOf())
        }
    private var _searchStickerList: List<SPStickerItem> = listOf()

    override val searchKeywordList: Observable<List<SPKeywordItem>> get() = _searchKeywordListChanges
    override val searchStickerList: Observable<List<SPStickerItem>>
        get() = Observable.combineLatest(
            _searchKeywordChanges, _searchKeywordListChanges
        ) { a, b ->
            listOf<SPStickerItem>()
        }

    override suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
        pageNumber: Int?
    ): SPPackageListResponse {
        return _remoteDatasource.stickerSearch(apikey, q, userId, lang, countryCode, limit, pageNumber)
    }

    override suspend fun trendingSearchTerms(
        apikey: String,
        lang: String?,
        countryCode: String?,
        limit: Int?
    ): SPKeywordListResponse {
        return _remoteDatasource.trendingSearchTerms(apikey, lang, countryCode, limit)
    }

    override suspend fun recentSearch(apikey: String, userId: String): SPKeywordListResponse {
        return _remoteDatasource.recentSearch(apikey, userId)
    }
}
