package io.stipop.refactor.domain.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil
import kotlin.math.round
import kotlin.math.roundToInt

abstract class PagingRepository<T> : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    val TAG: String? = this::class.simpleName

    protected var list: List<T>? = null
    protected var pageMap: SPPageMap? = null
    protected var hasLoading: Boolean = false

    protected val _listChanged: MutableLiveData<List<T>> = MutableLiveData<List<T>>()
    val listChanges: LiveData<List<T>> = MediatorLiveData<List<T>>().apply {
        addSource(_listChanged) {
            ArrayList<T>(list ?: listOf()).apply {
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

    protected fun getPageNumber(offset: Int?, pageMap: SPPageMap?): Int {
        val pageNumber: Int = pageMap?.let { pageMap ->
            offset?.let { offset ->
                val value = ceil((offset.toFloat() / pageMap.onePageCountRow.toFloat()) + 1f).toInt()

                if (value < pageMap.pageNumber) {
                    value
                } else {
                    value + 1
                }
            } ?: pageMap.pageNumber
        } ?: 1

        return pageNumber
    }

    protected fun getLimit(pageMap: SPPageMap?): Int {
        return pageMap?.onePageCountRow ?: 20
    }

    fun onReplaceItem(item: T) {
        Log.d(
            TAG, "onReplaceItem : \n" +
                    "item -> $item"
        )
        _listChanged.postValue(listOf(item))
    }

    fun onDeleteItem(item: T) {
        Log.d(
            TAG, "onDeleteItem : \n" +
                    "item -> $item"
        )

        list?.let {
            ArrayList(it)
                .run {
                    val index = indexOf(item)
                    if (index >= 0) {
                        removeAt(index)
                        list = this
                        _listChanged.postValue(list)
                    }
                }
        }
    }

    fun onSwapItem(sourceItem: T, destItem: T) {
        list?.let {
            ArrayList(it).run {

                val sourceIndex = indexOf(sourceItem)
                val destIndex = indexOf(destItem)

                if (sourceIndex >= 0 && destIndex >= 0) {
                    removeAt(sourceIndex)
                    add(destIndex, sourceItem)
                }
                list = this
                _listChanged.postValue(list)
            }
        }
    }

    protected fun getValidLoadPosition(list: List<T>?, pageMap: SPPageMap?, offset: Int): Boolean {
        return list?.let { list ->
            pageMap?.let { pageMap ->
                list.size - pageMap.onePageCountRow * 2 < offset
            } ?: false
        } ?: true
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

        val _offset = if (offset < 0) {
            list = null
            pageMap = null
            0
        } else {
            offset
        }

        if (
            !hasLoading
            && getValidLoadPosition(list, pageMap, _offset)
        ) {

            Log.d(
                this::class.simpleName, "onLoadMoreList : \n" +
                        "user -> $user \n" +
                        "keyword -> $keyword \n" +
                        "offset -> $offset \n" +
                        "limit -> $limit \n"
            )
                onLoadList(user, keyword, _offset, limit)
        }
    }

}
