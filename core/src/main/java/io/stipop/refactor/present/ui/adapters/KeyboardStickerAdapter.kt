package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import io.stipop.databinding.ItemStickerBinding
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.present.ui.listeners.OnItemSelectListener


class KeyboardStickerAdapter :
    ListAdapter<SPStickerItem, ItemStickerBinding>() {

    class KeyboardStickerViewHolder(override val binding: ItemStickerBinding) :
        ListAdapter.ListViewHolder<SPStickerItem, ItemStickerBinding>(
            binding
        ) {
        override fun onBind(item: SPStickerItem) {
            Glide.with(binding.stickerImage).load(item.stickerImg).into(binding.stickerImage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder<SPStickerItem, ItemStickerBinding> {
        return KeyboardStickerViewHolder(ItemStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}
