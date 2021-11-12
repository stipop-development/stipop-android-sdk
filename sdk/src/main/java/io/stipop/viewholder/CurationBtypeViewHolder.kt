package io.stipop.viewholder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.databinding.ItemCuratedCardTypeBBinding
import io.stipop.models.StickerPackage
import io.stipop.viewholder.delegates.StickerPackageClickDelegate

internal class CurationBtypeViewHolder(
    private val binding: ItemCuratedCardTypeBBinding,
    val delegate: StickerPackageClickDelegate?
) :
    RecyclerView.ViewHolder(binding.root) {

    private var stickerPackage: StickerPackage? = null

    init {
        itemView.setOnClickListener {
            stickerPackage?.packageId?.let {
                delegate?.onPackageDetailClicked(it, Constants.Point.CURATE_B)
            }
        }
    }

    fun bind(stickerPackage: StickerPackage) {
        this.stickerPackage = stickerPackage
        with(binding) {
            image.loadImage(stickerPackage.cardImgUrl ?: stickerPackage.packageImg)
            val color = Color.parseColor(Config.themeMainColor)
            val drawable = frame.background as GradientDrawable
            drawable.setColor(color)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: StickerPackageClickDelegate?
        ): CurationBtypeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_curated_card_type_b, parent, false)
            val binding = ItemCuratedCardTypeBBinding.bind(view)
            return CurationBtypeViewHolder(binding, delegate)
        }
    }
}