package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.stipop.refactor.domain.datasources.SearchDatasource
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.search.StickerSearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StickerDataRepository @Inject constructor(
    private val _remoteDatasource: SearchDatasource
) : StickerSearchRepository {

    private var _list: ArrayList<SPStickerItem>? = null
    private val _listChanged: PublishSubject<List<SPStickerItem>> = PublishSubject.create()
    override val list: List<SPStickerItem>?
        get() = _list

    override val listChanges: Observable<List<SPStickerItem>>
        get() = _listChanged.map { _changed ->
            arrayListOf<SPStickerItem>().apply {
                addAll(_list ?: listOf())
                _changed?.let {
                    it.forEach {
                        if (contains(it)) {
                            this[this.indexOf(it)] = it
                        } else {
                            this.add(it)
                        }
                    }
                    _list = this
                }
            }
        }

    private var _pageMap: SPPageMap? = null
    override val pageMap: SPPageMap?
        get() = _pageMap

    override fun onLoadList(
        user: SPUser,
        keyword: String,
        offset: Int?,
        limit: Int?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _remoteDatasource.stickerSearch(
                    user.apikey,
                    keyword,
                    user.userId,
                    user.language,
                    user.country,
                    limit,
                    getPageNumber(offset, pageMap)
                )
                    .run {
                        body.stickerList?.let {
                            _listChanged.onNext(it)
                        }
                    }
            } catch (e: Exception) {
                _listChanged.onNext(listOf())
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }
}
