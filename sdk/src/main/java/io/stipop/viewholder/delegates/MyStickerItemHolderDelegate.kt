package io.stipop.viewholder.delegates

import androidx.recyclerview.widget.RecyclerView

interface MyStickerItemHolderDelegate {
    fun onItemClicked(packageId: Int, entrancePoint: String)
    fun onItemLongClicked(position: Int)
    fun onVisibilityClicked(wantToVisible: Boolean, packageId: Int, position: Int)
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
    fun onDragCompleted(fromData: Any, toData: Any)
}