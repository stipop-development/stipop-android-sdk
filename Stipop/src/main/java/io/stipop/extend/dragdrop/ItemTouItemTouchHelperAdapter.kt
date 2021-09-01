package io.stipop.extend.dragdrop

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemRemove(position: Int)
    fun finishedDragAndDrop()
}
