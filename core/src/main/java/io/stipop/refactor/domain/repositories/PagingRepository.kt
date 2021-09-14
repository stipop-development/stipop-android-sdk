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

abstract class PagingRepository<T> : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    val TAG: String? = this::class.simpleName

    protected var list: ArrayList<T>? = null
    protected var pageMap: SPPageMap? = null
    protected var hasLoading: Boolean = false

    protected val _listChanged: MutableLiveData<List<T>> = MutableLiveData<List<T>>()
    val listChanges: LiveData<List<T>> = MediatorLiveData<List<T>>().apply {
        addSource(_listChanged) {
            newList ->

            (list ?: arrayListOf()).let {
                oldList ->

                newList.forEach {
                        item ->
                    if (oldList.contains(item)) {
                        oldList[oldList.indexOf(item)] = item
                    } else {
                        oldList.add(item)
                    }
                }

                list = oldList
                postValue(oldList)
            }

        }
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

    protected abstract fun onLoadList(
        user: SPUser,
        keyword: String,
        pageNumber: Int,
        limit: Int? = 20,
    )

    fun onLoadMoreList(
        user: SPUser,
        keyword: String,
        offset: Int,
        limit: Int? = 20,
    ) {

        val pageNumber: Int = if (offset < 0) {
            list = null
            pageMap = null
            1
        } else {
            pageMap?.let {
                ceil(offset.toFloat() / (it.onePageCountRow).toFloat()).toInt() + round((offset % it.onePageCountRow).toFloat() / it.onePageCountRow.toFloat()).toInt() + 1
            }?: 1
        }

        if (!hasLoading) {
            Log.e(TAG, "offset -> $offset")
            Log.e(TAG, "pageNumber -> $pageNumber")
            Log.d(
                this::class.simpleName, "onLoadMoreList : \n" +
                        "user -> $user \n" +
                        "keyword -> $keyword \n" +
                        "offset -> $offset \n" +
                        "limit -> $limit \n"
            )
            onLoadList(user, keyword, pageNumber, limit)
        }
    }

    fun onReloadList(
        user: SPUser,
        keyword: String,
        offset: Int,
        limit: Int? = 20,
    ) {

        val pageNumber = if (offset < 0) {
            list = null
            pageMap = null
            1
        } else {
            pageMap?.let {
                (offset.toFloat() / (it.onePageCountRow).toFloat()).toInt() + 1
            }?: 1
        }


        if (!hasLoading) {
            Log.e(TAG, "offset -> $offset")
            Log.e(TAG, "pageNumber -> $pageNumber")
            Log.e(
                this::class.simpleName, "onReloadList : \n" +
                        "user -> $user \n" +
                        "keyword -> $keyword \n" +
                        "offset -> $offset \n" +
                        "limit -> $limit \n"
            )
            onLoadList(user, keyword, pageNumber, limit)
        }
    }

}
