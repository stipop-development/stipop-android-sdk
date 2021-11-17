package io.stipop.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class HorizontalDecoration(
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int,
    private val itemSpacing: Int? = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = verticalSpacing
        outRect.bottom = verticalSpacing
        when {
            parent.getChildAdapterPosition(view) == 0 -> {
                outRect.left = horizontalSpacing
                outRect.right = itemSpacing ?: horizontalSpacing
            }
            parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1 -> {
                outRect.right = horizontalSpacing
            }
            else -> {
                outRect.right = itemSpacing ?: horizontalSpacing
            }
        }
    }
}