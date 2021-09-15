package io.stipop

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemMoveCompleted()
    fun onItemRemove(position: Int)
}