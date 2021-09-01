package io.stipop.refactor.data.repositories

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.SearchRestDatasource
import io.stipop.refactor.domain.entities.*
import io.stipop.refactor.domain.repositories.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    ): SPStickerListResponse {
        return _remoteDatasource.stickerSearch(apikey, q, userId, lang, countryCode, limit, pageNumber)
    }

    override suspend fun trendingSearchTerms(
        apikey: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?
    ): SPKeywordListResponse {
        return _remoteDatasource.trendingSearchTerms(apikey, userId, lang, countryCode, limit)
    }

    override suspend fun recentSearch(apikey: String, userId: String): SPKeywordListResponse {
        return _remoteDatasource.recentSearch(apikey, userId)
    }

    override fun onLoadSearchKeywordList(apikey: String, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            trendingSearchTerms(apikey, userId)
                .run {
                    body.keywordList?.let {
                        _searchKeywordListChanges.onNext(it)
                    }
                }
        }
    }

    override fun onLoadSearchStickerList(apikey: String,
                                         userId: String,
                                         keyword: String,
                                         lang: String?,
                                         countryCode: String?,
                                         limit: Int?,
                                         pageNumber: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            stickerSearch(apikey, keyword, userId, lang, countryCode, limit, pageNumber).run {
                body.stickerList?.let {
                    _searchStickerListChanges.onNext(it)
                }
            }
        }
    }
}
