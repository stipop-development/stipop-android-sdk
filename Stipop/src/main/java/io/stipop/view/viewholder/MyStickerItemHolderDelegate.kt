package io.stipop.view.viewholder

import androidx.recyclerview.widget.RecyclerView

interface MyStickerItemHolderDelegate {
    fun onItemClicked(position: Int)
    fun onItemLongClicked(position: Int)
    fun onVisibilityClicked(wantToVisible: Boolean, packageId: Int, position: Int)
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
    fun onDragCompleted(fromData: Any, toData: Any)
}