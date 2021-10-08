package io.stipop.viewholder

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.Utils
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
        with(binding){
            val drawable = container.background as GradientDrawable
            val color = Color.parseColor(Config.themeGroupedContentBackgroundColor)
            drawable.setColor(color)
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal_sticker_thumb, parent, false)
            val screenWidth = Utils.getScreenWidth(parent.context)
            val itemWidth = (screenWidth - Utils.dpToPx(48F) - (Utils.dpToPx(7F) * 3)) / 4
            val itemHeight = (75 * itemWidth) / 73
            view.layoutParams = ViewGroup.LayoutParams(itemWidth.toInt(), itemHeight.toInt())

            val binding = ItemHorizontalStickerThumbBinding.bind(view)
            return HorizontalStickerThumbViewHolder(binding)
        }
    }
}