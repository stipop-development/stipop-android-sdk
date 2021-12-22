package io.stipop.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.stipop.models.StickerPackage
import io.stipop.viewholder.VerticalStickerThumbViewHolder
import io.stipop.delegates.StickerPackageClickDelegate

internal class PagingPackageAdapter(private val delegate: StickerPackageClickDelegate, private val clickPoint: String) :
    PagingDataAdapter<StickerPackage, VerticalStickerThumbViewHolder>(REPO_COMPARATOR){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VerticalStickerThumbViewHolder {
        return VerticalStickerThumbViewHolder.create(parent, delegate, clickPoint)
    }

    override fun onBindViewHolder(holder: VerticalStickerThumbViewHolder, position: Int) {
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