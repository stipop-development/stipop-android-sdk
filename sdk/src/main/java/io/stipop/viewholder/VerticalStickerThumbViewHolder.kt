package io.stipop.viewholder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Constants
import io.stipop.R
import io.stipop.databinding.ItemVerticalStickerThumbBinding
import io.stipop.models.StickerPackage
import io.stipop.view_common.StickerPackageActivity

class VerticalStickerThumbViewHolder(private val binding: ItemVerticalStickerThumbBinding) :
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
            stickerPackageThumb.loadImage(stickerPackage.packageImg)
            packageNameTextView.text = stickerPackage.packageName
            artistNameTextView.text = stickerPackage.artistName
        }
    }

    companion object {
        fun create(parent: ViewGroup): VerticalStickerThumbViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vertical_sticker_thumb, parent, false)
            val binding = ItemVerticalStickerThumbBinding.bind(view)
            return VerticalStickerThumbViewHolder(binding)
        }
    }
}