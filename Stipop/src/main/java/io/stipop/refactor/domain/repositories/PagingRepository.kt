package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.Config.Companion.apikey
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPUser

interface PagingRepository<T> {
    val list: List<T>?
    val listChanges: Observable<List<T>>
    val pageMap: SPPageMap?

    fun getPageNumber(offset: Int?, pageMap: SPPageMap?): Int {
        return pageMap?.let { _pageMap ->
            offset?.let { _offset ->
                _offset / _pageMap.onePageCountRow
            }
        } ?: 1
    }

    fun getLimit(pageMap: SPPageMap?): Int {
        return pageMap?.onePageCountRow ?: 20
    }

    val hasMore: Boolean
        get() {
            return list?.let { list ->
                pageMap?.let { pageMap ->
                    pageMap.totalCount > list.size
                } ?: false
            } ?: true
        }

    val isEmpty: Boolean
        get() {
            return list == null || (list?.isEmpty() ?: false && !hasMore)
        }

    fun onLoadList(
        user: SPUser,
        keyword: String,
        offset: Int?,
        limit: Int? = 20,
    )

    fun onLoadMoreList(
        user: SPUser,
        keyword: String,
        offset: Int,
        limit: Int? = 20,
    ) {
        if (hasMore) {
            onLoadList(user, keyword, offset, limit)
        }
    }
}
