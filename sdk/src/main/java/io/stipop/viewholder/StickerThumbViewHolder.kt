package io.stipop.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.R
import io.stipop.adapter.StickerThumbAdapter
import io.stipop.databinding.ItemStickerThumbBinding
import io.stipop.models.SPSticker

internal class StickerThumbViewHolder(
    private val binding: ItemStickerThumbBinding,
    val delegate: StickerThumbAdapter.OnStickerClickListener? = null
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
        with(binding) {
            Glide.with(itemView.context).load(sticker.stickerImgLocalFilePath ?: sticker.stickerImg)
                .into(imageView)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: StickerThumbAdapter.OnStickerClickListener? = null
        ): StickerThumbViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sticker_thumb, parent, false)
            val binding = ItemStickerThumbBinding.bind(view)
            return StickerThumbViewHolder(binding, delegate)
        }
    }
}