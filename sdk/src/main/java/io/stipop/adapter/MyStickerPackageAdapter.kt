package io.stipop.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.stipop.ItemTouchHelperDelegate
import io.stipop.models.StickerPackage
import io.stipop.viewholder.delegates.MyStickerClickDelegate
import io.stipop.viewholder.MyStickerPackageViewHolder

internal class MyStickerPackageAdapter(private val delegate: MyStickerClickDelegate) :
    PagingDataAdapter<StickerPackage, MyStickerPackageViewHolder>(REPO_COMPARATOR),
    ItemTouchHelperDelegate {

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
