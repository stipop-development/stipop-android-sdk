package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemStickerBinding
import io.stipop.refactor.domain.entities.SPStickerItem

class SearchStickerAdapter :
    ViewBindingAdapter<SPStickerItem, ItemStickerBinding>() {

    class SearchStickerViewHolder(binding: ItemStickerBinding) :
        ViewBindingHolder<SPStickerItem, ItemStickerBinding>(binding) {

        override fun onBind(item: SPStickerItem) {

            Glide.with(itemView)
                .load(item.stickerImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.stickerImage)
                .clearOnDetach()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<SPStickerItem, ItemStickerBinding> {
        binding = ItemStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchStickerViewHolder(binding)
    }
}
