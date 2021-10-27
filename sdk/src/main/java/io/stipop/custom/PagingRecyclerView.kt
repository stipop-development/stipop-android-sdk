package io.stipop.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class PagingRecyclerView : RecyclerView {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    var paging = MutableSharedFlow<Int>()
    private var page = 1
    private var expectingItemCount = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun refresh() {
        scope.launch {
            page = 1
            paging.emit(page)
        }
    }

    fun setUpScrollListener() {
        val layoutManager = layoutManager as LinearLayoutManager
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
//                val isShouldFetchMore = lastVisibleItemPosition + 1 == totalItemCount
                val isShouldFetchMore =
                    (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) && expectingItemCount < totalItemCount
                if (isShouldFetchMore) {
                    expectingItemCount += Constants.ApiParams.SizePerPage
                    scope.launch {
                        page += 1
                        paging.emit(page)
                    }
                }
            }
        })
    }
}