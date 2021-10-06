package io.stipop.viewholder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.adapter.HorizontalStickerThumbnailAdapter
import io.stipop.databinding.ItemHorizontalStickerThumbContainerBinding
import io.stipop.models.StickerPackage
import io.stipop.setStipopUnderlineColor

class HorizontalStickerThumbContainerViewHolder(private val binding: ItemHorizontalStickerThumbContainerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val adapter: HorizontalStickerThumbnailAdapter by lazy { HorizontalStickerThumbnailAdapter() }

    init {
        with(binding) {
            underLine.setStipopUnderlineColor()
            titleTextView.setTextColor(Config.getTitleTextColor(itemView.context))
        }
    }

    fun bind(title: String, stickerPackages: List<StickerPackage>) {
        Log.d("STIPOP-DEBUG", "Trending 개수 : ${stickerPackages.size}")
        with(binding) {
            titleTextView.text = title
            recyclerView.adapter = adapter
            adapter.updateData(stickerPackages)
        }
    }

    companion object {
        fun create(parent: ViewGroup): HorizontalStickerThumbContainerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_horizontal_sticker_thumb_container, parent, false)
            val binding = ItemHorizontalStickerThumbContainerBinding.bind(view)
            return HorizontalStickerThumbContainerViewHolder(binding)
        }
    }
}