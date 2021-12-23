package io.stipop.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.R
import io.stipop.adapter.StickerDefaultAdapter
import io.stipop.databinding.ItemStickerThumbBinding
import io.stipop.models.SPSticker

internal class StickerThumbViewHolder(
    private val binding: ItemStickerThumbBinding,
    val delegate: StickerDefaultAdapter.OnStickerClickListener? = null
) :
    RecyclerView.ViewHolder(binding.root) {

    private var spSticker: SPSticker? = null

    init {
        itemView.setOnClickListener {
            spSticker?.let {
                delegate?.onStickerClick(absoluteAdapterPosition, it)
            }

        }
    }

    fun bind(sticker: SPSticker) {
        spSticker = sticker
        binding.imageView.loadImage(sticker.stickerImgLocalFilePath?:sticker.stickerImg, false)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: StickerDefaultAdapter.OnStickerClickListener? = null
        ): StickerThumbViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sticker_thumb, parent, false)
            val binding = ItemStickerThumbBinding.bind(view)
            return StickerThumbViewHolder(binding, delegate)
        }
    }
}