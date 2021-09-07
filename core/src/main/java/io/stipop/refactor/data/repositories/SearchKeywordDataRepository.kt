package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.SearchRestDatasource
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.SearchKeywordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SearchKeywordDataRepository @Inject constructor(
    private val _remoteDatasource: SearchRestDatasource
) : SearchKeywordRepository {
    override var list: List<SPKeywordItem>? = null
    val _listChanged: BehaviorSubject<List<SPKeywordItem>> = BehaviorSubject.create()
    override val listChanges: Observable<List<SPKeywordItem>>
        get() = _listChanged.map {
            arrayListOf<SPKeywordItem>().apply {
                addAll(list ?: listOf())

                it?.forEach {

                    if (this.contains(it)) {
                        this[this.indexOf(it)] = it
                    } else {
                        this.add(it)
                    }
                }

                list = this
            }
        }

    override var pageMap: SPPageMap? = null

    override fun onLoadList(user: SPUser, keyword: String, offset: Int?, limit: Int?) {
        Log.d(
            this::class.simpleName, "onLoadList : \n " +
                    "user -> $user \n" +
                    "keyword -> $keyword \n" +
                    "offset -> $offset \n" +
                    "limit -> $limit \n" +
                    "pageNumber -> ${getPageNumber(offset, pageMap)} \n" +
                    ""
        )

        runBlocking(Dispatchers.IO) {
            try {
                _remoteDatasource.trendingSearchTerms(
                    user.apikey,
                    keyword,
                    user.userId,
                    user.language,
                    limit,
                )
                    .run {
                        body.keywordList?.let {
                            if (it.isNotEmpty()) {
                                _listChanged.onNext(it)
                            }
                        }
                    }
            } catch (e: Exception) {
                _listChanged.onNext(listOf())
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }

}
