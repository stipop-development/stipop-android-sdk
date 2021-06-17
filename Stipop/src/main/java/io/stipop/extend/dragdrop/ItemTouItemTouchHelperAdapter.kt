package io.stipop

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemRemove(position: Int)
    fun finishedDragAndDrop()
}