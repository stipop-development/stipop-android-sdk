package io.stipop.adapter.viewholder

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.custom.StipopImageView
import io.stipop.event.MyPackEventDelegate
import io.stipop.models.StickerPackage

internal class MyPackThumbViewHolder(view: View, val delegate: MyPackEventDelegate?) : RecyclerView.ViewHolder(view) {
    private val imageIV: StipopImageView = view.findViewById(R.id.imageIV)
    private val containerLL: LinearLayout = view.findViewById(R.id.containerLL)
    private var stickerPackage: StickerPackage? = null

    init {
        itemView.setOnClickListener {
            stickerPackage?.let {
                delegate?.onPackageClick(bindingAdapterPosition, it)
            }
        }
//        itemView.setOnLongClickListener {
//            delegate?.onDragStarted(this)
//            return@setOnLongClickListener true
//        }
    }

    fun bindData(data: StickerPackage?, isSelected: Boolean = false) {
        stickerPackage = data
        Glide.with(itemView.context).load(stickerPackage?.packageImg).dontAnimate().into(imageIV)
        setSelectFilter(isSelected)
    }

    private fun setSelectFilter(isSelected: Boolean) {
        val matrix = ColorMatrix()
        when (isSelected) {
            true -> {
                containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
                matrix.setSaturation(1.0f)
            }
            false -> {
                containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
                matrix.setSaturation(0.0f)
            }
        }
        imageIV.colorFilter = ColorMatrixColorFilter(matrix)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: MyPackEventDelegate?
        ): MyPackThumbViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_pack_thumb, parent, false)
            return MyPackThumbViewHolder(view, delegate)
        }
    }
}