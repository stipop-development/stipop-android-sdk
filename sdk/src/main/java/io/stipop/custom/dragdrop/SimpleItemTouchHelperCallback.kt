package io.stipop.custom.dragdrop

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.ItemTouchHelperDelegate

internal class SimpleItemTouchHelperCallback(val delegate: ItemTouchHelperDelegate) : ItemTouchHelper.Callback() {

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags: Int
        val swipeFlags: Int
        if (recyclerView.layoutManager is LinearLayoutManager) {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            swipeFlags = 0
        } else {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return delegate.onItemMove(source.bindingAdapterPosition, target.bindingAdapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
        delegate.onItemRemove(viewHolder.bindingAdapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            delegate.onItemMoveCompleted()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }
}
