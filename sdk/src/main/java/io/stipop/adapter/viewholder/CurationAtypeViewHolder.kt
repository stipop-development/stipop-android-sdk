package io.stipop.adapter.viewholder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.databinding.ItemCuratedCardTypeABinding
import io.stipop.models.StickerPackage
import io.stipop.event.PackClickDelegate

internal class CurationAtypeViewHolder(
    private val binding: ItemCuratedCardTypeABinding,
    val delegate: PackClickDelegate?
) :
    RecyclerView.ViewHolder(binding.root) {

    private var stickerPackage: StickerPackage? = null

    init {
        itemView.setOnClickListener {
            stickerPackage?.packageId?.let {
                delegate?.onPackageDetailClicked(it, Constants.Point.CURATE_A)
            }
        }
    }

    fun bind(stickerPackage: StickerPackage) {
        this.stickerPackage = stickerPackage
        with(binding) {
            val colorCode = if (Config.themeUseLightMode) {
                stickerPackage.lightBackgroundCode
            } else {
                stickerPackage.darkBackgroundCode
            }
            val color = Color.parseColor(colorCode ?: Config.themeGroupedContentBackgroundColor)
            val drawable = container.background as GradientDrawable
            drawable.setColor(color)
            image.loadImage(stickerPackage.packageImg, false)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: PackClickDelegate?
        ): CurationAtypeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_curated_card_type_a, parent, false)
            val binding = ItemCuratedCardTypeABinding.bind(view)
            return CurationAtypeViewHolder(binding, delegate)
        }
    }
}