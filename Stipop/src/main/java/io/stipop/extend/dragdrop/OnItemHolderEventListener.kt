package io.stipop.extend.dragdrop

import androidx.recyclerview.widget.RecyclerView

interface OnItemHolderEventListener {
    fun onItemClicked(position: Int)
    fun onItemLongClicked(position: Int)
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
}