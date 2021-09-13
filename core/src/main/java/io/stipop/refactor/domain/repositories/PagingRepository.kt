package io.stipop.refactor.domain.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class PagingRepository<T> : CoroutineScope {

    val TAG: String? = this::class.simpleName

    var list: List<T>? = null
    var pageMap: SPPageMap? = null
    var hasLoading: Boolean = false

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

    fun getPageNumber(offset: Int?, pageMap: SPPageMap?): Int {
        return (pageMap?.pageNumber ?: 0)
    }

    fun getLimit(pageMap: SPPageMap?): Int {
        return pageMap?.onePageCountRow ?: 20
    }

    fun onReplaceItem(item: T) {
        _listChanged.postValue(listOf(item))
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
            launch(Dispatchers.IO) {
                Log.e(TAG, "has loading -> ${hasLoading}")
                hasLoading = true

                onLoadList(user, keyword, _offset, limit)

                Log.e(TAG, "has loading -> ${hasLoading}")
            }
        }
    }

    private fun getValidLoadPosition(list: List<T>?, pageMap: SPPageMap?, offset: Int): Boolean {
        return list?.let { list ->
            pageMap?.let { pageMap ->
                list.size - pageMap.onePageCountRow * 2 < offset
            } ?: false
        } ?: true
    }

    override val coroutineContext: CoroutineContext = Dispatchers.IO
}
