package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemStickerBinding
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem

class KeyboardStickerAdapter :
    ListAdapter<SPStickerItem, KeyboardStickerAdapter.KeyboardStickerViewHolder>(object :
        DiffUtil.ItemCallback<SPStickerItem>() {
        override fun areItemsTheSame(oldItem: SPStickerItem, newItem: SPStickerItem): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: SPStickerItem, newItem: SPStickerItem): Boolean = oldItem == newItem
    }) {

    class KeyboardStickerViewHolder(override val binding: ItemStickerBinding) :
        ViewBindingAdapter.ViewBindingHolder<SPStickerItem>(
            binding
        ) {
        override fun onBind(item: SPStickerItem) {
            Glide.with(itemView).load(item.stickerImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.stickerImage)
        }
    }

    var itemClick: ((SPStickerItem) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KeyboardStickerViewHolder {
        return KeyboardStickerViewHolder(ItemStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            .apply {
                binding.root.setOnClickListener {
                    itemClick?.invoke(getItem(absoluteAdapterPosition))
                }
            }
    }

    override fun onBindViewHolder(holder: KeyboardStickerAdapter.KeyboardStickerViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
