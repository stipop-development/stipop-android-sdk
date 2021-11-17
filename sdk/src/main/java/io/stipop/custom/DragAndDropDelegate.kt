package io.stipop.custom

internal interface DragAndDropDelegate {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemMoveCompleted()
    fun onItemRemove(position: Int)
}