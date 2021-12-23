package io.stipop.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.stipop.models.StickerPackage
import io.stipop.adapter.viewholder.PackFullWidthViewHolder
import io.stipop.event.PackClickDelegate

internal class PagingPackageAdapter(private val delegate: PackClickDelegate, private val clickPoint: String) :
    PagingDataAdapter<StickerPackage, PackFullWidthViewHolder>(REPO_COMPARATOR){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackFullWidthViewHolder {
        return PackFullWidthViewHolder.create(parent, delegate, clickPoint)
    }

    override fun onBindViewHolder(holder: PackFullWidthViewHolder, position: Int) {
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
}