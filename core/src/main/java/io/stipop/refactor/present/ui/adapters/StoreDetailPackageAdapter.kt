package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemStickerBinding
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.domain.entities.SPStickerItem

class StoreDetailPackageAdapter : ListAdapter<SPStickerItem, StoreDetailPackageAdapter.StoreDetailPackageViewHolder>(
    object : DiffUtil.ItemCallback<SPStickerItem>() {
        override fun areItemsTheSame(oldItem: SPStickerItem, newItem: SPStickerItem): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: SPStickerItem, newItem: SPStickerItem): Boolean = oldItem == newItem
    }
) {

    class StoreDetailPackageViewHolder(private val _binding: ItemStickerBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        fun onBind(item: SPStickerItem) {
            Glide.with(itemView)
                .load(item.stickerImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(_binding.stickerImage)
                .clearOnDetach()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreDetailPackageViewHolder {
        return StoreDetailPackageViewHolder(
            ItemStickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoreDetailPackageViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}

