package io.stipop.ui.components.core.item_decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemPaddingDecoration(private val _padding: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.set(_padding / 2, _padding / 2, _padding / 2, _padding / 2)
    }
}
