package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.data.datasources.SearchRestDatasource
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.SearchStickerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SearchStickerDataRepository @Inject constructor(
    private val _remoteDatasource: SearchRestDatasource
) : SearchStickerRepository() {

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
                _remoteDatasource.stickerSearch(
                    user.apikey,
                    keyword,
                    user.userId,
                    user.language,
                    user.country,
                    limit,
                    getPageNumber(offset, pageMap) + 1
                )
                    .run {
                        body.stickerList?.let {
                            if (it.isNotEmpty()) {
                                pageMap = body.pageMap
                                _listChanged.postValue(it)
                            }
                        }
                    }
            } catch (e: Exception) {
                _listChanged.postValue(listOf())
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }

}
