package io.stipop.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.stipop.adapter.viewholder.StickerThumbViewHolder
import io.stipop.models.Sticker

internal class PagingStickerAdapter(
    private val delegate: StickerDefaultAdapter.OnStickerClickListener,
    private val isLockable: Boolean
    ) : PagingDataAdapter<Sticker, StickerThumbViewHolder>(REPO_COMPARATOR){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StickerThumbViewHolder {
        return StickerThumbViewHolder.create(parent, delegate, isLockable)
    }

    override fun onBindViewHolder(holder: StickerThumbViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem.toSPSticker())
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Sticker>() {
            override fun areItemsTheSame(
                oldItem: Sticker,
                newItem: Sticker
            ): Boolean =
                (oldItem.stickerId == newItem.stickerId)

            override fun areContentsTheSame(
                oldItem: Sticker,
                newItem: Sticker
            ): Boolean =
                oldItem == newItem
        }
    }
}