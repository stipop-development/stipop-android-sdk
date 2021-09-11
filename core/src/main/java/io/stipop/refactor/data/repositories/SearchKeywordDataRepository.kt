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
import kotlinx.coroutines.*
import javax.inject.Inject

class SearchKeywordDataRepository @Inject constructor(
    private val _remoteDatasource: SearchRestDatasource
) : SearchKeywordRepository() {

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
        launch {
            hasLoading = coroutineContext.isActive
            _remoteDatasource.trendingSearchTerms(
                user.apikey,
                user.userId,
                user.language,
                user.country,
                limit,
            )
                .run {
                    body.keywordList.let {
                        _listChanged.postValue(it ?: listOf())
                    }
                }
            cancel()
            hasLoading = coroutineContext.isActive
        }

    }

}
