package io.stipop.refactor.data.repositories

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.SearchRestDatasource
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchDataRepository @Inject constructor(
    private val _remoteDatasource: SearchRestDatasource
) : SearchRepository {
    private val _recentSearchKeywordListChanges: BehaviorSubject<List<SPKeywordItem>> =
        BehaviorSubject.create<List<SPKeywordItem>?>().apply {
            onNext(listOf())
        }
    private val _recentSearchKeywordList: ArrayList<SPKeywordItem> = arrayListOf()
    override val recentSearchKeywordList: Observable<List<SPKeywordItem>>
        get() = _recentSearchKeywordListChanges.map {
            it.forEach {
                if (_recentSearchKeywordList.contains(it)) {
                    _recentSearchKeywordList[_recentSearchKeywordList.indexOf(it)] = it
                } else {
                    _recentSearchKeywordList.add(it)
                }
            }
            _recentSearchKeywordList
        }

    private var _searchKeywordListChanges: BehaviorSubject<List<SPKeywordItem>> =
        BehaviorSubject.create<List<SPKeywordItem>?>().apply {
            onNext(listOf())
        }
    private var _searchKeywordList: ArrayList<SPKeywordItem> = arrayListOf()
    override val searchKeywordList: Observable<List<SPKeywordItem>>
        get() = _searchKeywordListChanges.map {
            it.forEach {
                if (_searchKeywordList.contains(it)) {
                    _searchKeywordList[_searchKeywordList.indexOf(it)] = it
                } else {
                    _searchKeywordList.add(it)
                }
            }
            _searchKeywordList
        }


    private var _searchStickerListChanges: BehaviorSubject<List<SPStickerItem>> =
        BehaviorSubject.create<List<SPStickerItem>?>().apply {
            onNext(listOf())
        }
    private val _searchStickerList: ArrayList<SPStickerItem> = arrayListOf()
    override val searchStickerList: Observable<List<SPStickerItem>>
        get() = _searchStickerListChanges.map {
            it.forEach {
                if (_searchStickerList.contains(it)) {
                    _searchStickerList[_searchStickerList.indexOf(it)] = it
                } else {
                    _searchStickerList.add(it)
                }
            }
            _searchStickerList
        }
    override val searchStickerListPageMap: Observable<SPPageMap>
        get() = TODO("Not yet implemented")


    override fun onLoadSearchKeywordList(
        apikey: String,
        userId: String,
        language: String?,
        countryCode: String?,
        limit: Int?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            _remoteDatasource.trendingSearchTerms(apikey, userId, language, countryCode, limit)
                .run {
                    body.keywordList?.let {
                        _searchKeywordListChanges.onNext(it)
                    }
                }
        }
    }

    override fun onLoadRecentSearchKeywordList(apikey: String, userId: String) {
        TODO("Not yet implemented")
    }

    override fun onLoadSearchStickerList(
        apikey: String,
        userId: String,
        keyword: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
        pageNumber: Int?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            _remoteDatasource.stickerSearch(apikey, keyword, userId, lang, countryCode, limit, pageNumber).run {
                body.stickerList?.let {
                    _searchStickerListChanges.onNext(it)
                }
            }
        }
    }
}
