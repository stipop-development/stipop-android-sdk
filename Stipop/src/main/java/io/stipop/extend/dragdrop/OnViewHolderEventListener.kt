package io.stipop.extend.dragdrop

import androidx.recyclerview.widget.RecyclerView

interface OnViewHolderEventListener {
    fun onItemClicked(position: Int)
    fun onItemLongClicked(position: Int)
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
    fun onDragCompleted(fromData: Any, toData: Any)
}