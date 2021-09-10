package io.stipop.refactor.domain.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

abstract class PagingRepository<T> {
    var list: List<T>? = null
    var pageMap: SPPageMap? = null

    protected val _listChanged: MutableLiveData<List<T>> = MutableLiveData<List<T>>()
    val listChanges: LiveData<List<T>> = MediatorLiveData<List<T>>().apply {
        addSource(_listChanged) {
            arrayListOf<T>().apply {
                addAll(list ?: listOf())
                it?.let {
                    it.forEach {
                        if (contains(it)) {
                            this[this.indexOf(it)] = it
                        } else {
                            this.add(it)
                        }
                    }
                    list = this
                }
            }.run {
                postValue(this)
            }
        }
    }

    fun getPageNumber(offset: Int?, pageMap: SPPageMap?): Int {
        return (pageMap?.pageNumber ?: 0)
    }

    fun getLimit(pageMap: SPPageMap?): Int {
        return pageMap?.onePageCountRow ?: 20
    }

    fun getHasMore(list: List<T>?, pageMap: SPPageMap?, offset: Int): Boolean {
        return list?.let {

            pageMap?.let {

                pageMap.pageCount > pageMap.pageNumber

            } ?: false


        } ?: true
    }

    val isEmpty: Boolean
        get() {
            return list?.isEmpty() ?: true
        }

    protected abstract fun onLoadList(
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
        Log.d(this::class.simpleName, "onLoadMoreList : \n" +
                "user -> $user \n" +
                "keyword -> $keyword \n" +
                "offset -> $offset \n" +
                "limit -> $limit \n"
        )

        val _offset = if (offset < 0) {
            list = null
            pageMap = null
            0
        } else {
            offset
        }

        if (getHasMore(list, pageMap, _offset) && getValidLoadPosition(list, pageMap, _offset)) {
            runBlocking(Dispatchers.IO) {
                onLoadList(user, keyword, _offset, limit)
            }
        }
    }

    fun getValidLoadPosition(list: List<T>?, pageMap: SPPageMap?, offset: Int): Boolean {
        return list?.let {

                list ->

            pageMap?.let {

                    pageMap ->

                list.size - pageMap.onePageCountRow * 2 < offset


            } ?: false


        } ?: true
    }
}
