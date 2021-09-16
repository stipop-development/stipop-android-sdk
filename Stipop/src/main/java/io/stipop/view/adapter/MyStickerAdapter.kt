package io.stipop.view.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.stipop.ItemTouchHelperAdapter
import io.stipop.extend.dragdrop.OnViewHolderEventListener
import io.stipop.models.StickerPackage

class MyStickerAdapter(private val delegate: OnViewHolderEventListener) :
    PagingDataAdapter<StickerPackage, MyStickerPackageViewHolder>(REPO_COMPARATOR),
    ItemTouchHelperAdapter {

    var fromData: StickerPackage? = null
    var toData: StickerPackage? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStickerPackageViewHolder {
        return MyStickerPackageViewHolder.create(parent, delegate)
    }

    override fun onBindViewHolder(holder: MyStickerPackageViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<StickerPackage>() {
            override fun areItemsTheSame(
                oldItem: StickerPackage,
                newItem: StickerPackage
            ): Boolean =
                (oldItem.packageId == newItem.packageId && oldItem.getIsVisible() == newItem.getIsVisible())

            override fun areContentsTheSame(
                oldItem: StickerPackage,
                newItem: StickerPackage
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromData == null) {
            fromData = getItem(fromPosition)
        }
        toData = getItem(toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemRemove(position: Int) {
//        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMoveCompleted() {
        if (fromData != null && toData != null && fromData != toData) {
            delegate.onDragCompleted(fromData!!, toData!!)
            fromData = null
            toData = null
        }
    }
}
