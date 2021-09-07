package io.stipop.extend.dragdrop

import androidx.recyclerview.widget.RecyclerView

interface OnRecyclerAdapterEventListener {
    fun onItemClicked(position: Int)
    fun onItemLongClicked(position: Int)
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
}