package io.stipop.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class PagingRecyclerView : RecyclerView {

    private val VISIBLE_THRESHOLD = 5
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    val paging = MutableSharedFlow<Int>()
    private var page = 1

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setUpScrollListener() {
        val layoutManager = layoutManager as LinearLayoutManager
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val isShouldFetchMore = lastVisibleItemPosition + 1 == totalItemCount
                if (isShouldFetchMore) {
                    scope.launch {
                        page += 1
                        paging.emit(page)
                    }
                }
            }
        })
    }
}