package io.stipop.extend.dragdrop

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.ItemTouchHelperAdapter

class SimpleItemTouchHelperCallback(val adapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {
    private val MAX_ALPHA = 1.0f

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }


    override fun getMovementFlags(recyclerView: RecyclerView,
                                  viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags: Int
        var swipeFlags: Int

        if (recyclerView.layoutManager is LinearLayoutManager) {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            swipeFlags = 0
        } else {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView,
                        source: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        if (source.itemViewType != target.itemViewType) {
            return false
        }

        adapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
        adapter.onItemRemove(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int,
                             isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val alpha = MAX_ALPHA - Math.abs(dX) / viewHolder.itemView.width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                   actionState: Int) {

        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            adapter.finishedDragAndDrop()
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder:
    RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        viewHolder.itemView.alpha = MAX_ALPHA

        if (viewHolder is ItemTouchHelperViewHolder) {
            viewHolder.onItemClear()
        }
    }
}
