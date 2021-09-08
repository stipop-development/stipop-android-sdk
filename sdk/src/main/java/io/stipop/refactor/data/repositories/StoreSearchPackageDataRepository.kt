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

class StoreSearchPackageDataRepository @Inject constructor(
    private val _remoteDatasource: SearchRestDatasource
) : SearchStickerRepository {
    override var list: List<SPStickerItem>? = null
    val _listChanged: BehaviorSubject<List<SPStickerItem>> = BehaviorSubject.create()
    override val listChanges: Observable<List<SPStickerItem>>
        get() = _listChanged.map {
            arrayListOf<SPStickerItem>().apply {
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
