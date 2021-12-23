package io.stipop.adapter.viewholder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.stipop.*
import io.stipop.Config
import io.stipop.Constants
import io.stipop.StipopUtils
import io.stipop.databinding.ItemCuratedCardTypeBBinding
import io.stipop.models.StickerPackage
import io.stipop.event.PackClickDelegate

internal class CurationBtypeViewHolder(
    private val binding: ItemCuratedCardTypeBBinding,
    val delegate: PackClickDelegate?
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
            val colorCode = if (Config.themeUseLightMode) {
                stickerPackage.lightBackgroundCode
            } else {
                stickerPackage.darkBackgroundCode
            }
            val color = Color.parseColor(colorCode ?: Config.themeMainColor)
            val drawable = frame.background as GradientDrawable
            drawable.setColor(color)
            Glide.with(itemView.context)
                .load(stickerPackage.cardImgUrl ?: stickerPackage.packageImg)
                .transform(CenterCrop(), RoundedCorners(StipopUtils.pxToDp(7).toInt())).into(image)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: PackClickDelegate?
        ): CurationBtypeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_curated_card_type_b, parent, false)
            val binding = ItemCuratedCardTypeBBinding.bind(view)
            return CurationBtypeViewHolder(binding, delegate)
        }
    }
}