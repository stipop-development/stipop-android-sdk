package io.stipop.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StickerDecoration(private val spacingValue: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = spacingValue
        outRect.bottom = spacingValue
        outRect.left = spacingValue
        outRect.right = spacingValue
    }
}