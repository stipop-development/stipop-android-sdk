package io.stipop.viewholder

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Constants
import io.stipop.R
import io.stipop.databinding.ItemDefaultBinding
import io.stipop.databinding.ItemHorizontalStickerThumbBinding
import io.stipop.databinding.ItemLoadStateFooterViewBinding
import io.stipop.models.StickerPackage
import io.stipop.view_common.StickerPackageActivity

class HorizontalStickerThumbViewHolder(private val binding: ItemHorizontalStickerThumbBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private var stickerPackage: StickerPackage? = null

    init {
        itemView.setOnClickListener {
            stickerPackage?.packageId?.let {
                Intent(itemView.context, StickerPackageActivity::class.java).apply {
                    putExtra(Constants.IntentKey.PACKAGE_ID, it)
                }.run {
                    itemView.context.startActivity(this)
                }
            }
        }
    }

    fun bind(stickerPackage: StickerPackage) {
        this.stickerPackage = stickerPackage
        with(binding) {
            image.loadImage(stickerPackage.packageImg)
        }
    }

    companion object {
        fun create(parent: ViewGroup): HorizontalStickerThumbViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_horizontal_sticker_thumb, parent, false)
            val binding = ItemHorizontalStickerThumbBinding.bind(view)
            return HorizontalStickerThumbViewHolder(binding)
        }
    }
}