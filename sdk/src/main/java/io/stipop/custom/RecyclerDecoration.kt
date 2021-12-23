package io.stipop.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class RecyclerDecoration(private val divHeight: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1)
            outRect.right = divHeight
    }

}