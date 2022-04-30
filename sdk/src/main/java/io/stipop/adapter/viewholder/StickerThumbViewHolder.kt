package io.stipop.adapter.viewholder

import android.os.Handler
import android.os.Looper
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
        var i: Int = 0
        var singleCount: Int = 0
        var doubleCount: Int = 0
        var doubleTap: Boolean = false
        var singleTap: Boolean = false

        itemView.setOnClickListener {
            i += 1
            if (i == 2) {
                doubleTap = true
                singleTap = false
                i = 0
            } else if (i == 1) {
                doubleTap = false
                singleTap = true
            } else {
                doubleTap = false
                singleTap = false
                i = 0
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (singleTap) {
                    if (singleCount % 3 == 0) {
                        spSticker?.let {
                            delegate?.onStickerClick(absoluteAdapterPosition, it)
                        }
                    } else {
                        singleCount += 1
                    }
                } else if (doubleTap) {
                    if (doubleCount == 1) {
                        spSticker?.let {
                            delegate?.onStickerDoubleTap(absoluteAdapterPosition, it)
                        }
                        doubleCount = 0
                    } else {
                        doubleCount += 1
                    }
                }
                i = 0
            }, 300)
        }

    }

    fun bind(sticker: SPSticker) {
        spSticker = sticker
        binding.imageView.loadImage(sticker.stickerImgLocalFilePath ?: sticker.stickerImg, false)
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