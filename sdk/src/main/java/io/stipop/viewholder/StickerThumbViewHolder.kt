package io.stipop.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.R
import io.stipop.databinding.ItemStickerThumbBinding
import io.stipop.models.Sticker

class StickerThumbViewHolder(private val binding: ItemStickerThumbBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            //
        }
    }

    fun bind(sticker: Sticker) {
        with(binding) {
            Glide.with(itemView.context).load(sticker.stickerImgLocalFilePath ?: sticker.stickerImg)
                .into(imageView)
        }
    }

    companion object {
        fun create(parent: ViewGroup): StickerThumbViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sticker_thumb, parent, false)
            val binding = ItemStickerThumbBinding.bind(view)
            return StickerThumbViewHolder(binding)
        }
    }
}