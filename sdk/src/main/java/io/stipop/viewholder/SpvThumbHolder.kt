package io.stipop.viewholder

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.adapter.SpvPackageAdapter
import io.stipop.custom.StipopImageView
import io.stipop.models.StickerPackage

internal class SpvThumbHolder(view: View, val delegate: SpvPackageAdapter.OnPackageClickListener) :
    RecyclerView.ViewHolder(view) {
    private val imageIV: StipopImageView = view.findViewById(R.id.imageIV)
    private val containerLL: LinearLayout = view.findViewById(R.id.containerLL)
    private var stickerPackage: StickerPackage? = null

    init {
        itemView.setOnClickListener {
            stickerPackage?.let {
                (bindingAdapter as SpvPackageAdapter).updateSelected(bindingAdapterPosition)
                setSelectFilter(true)
                delegate.onPackageClick(bindingAdapterPosition, it)
            }
        }
    }

    fun bindData(data: StickerPackage) {
        stickerPackage = data
        Glide.with(itemView.context).load(stickerPackage?.packageImg).dontAnimate().into(imageIV)
        setSelectFilter(false)
//        if (stickerPackage?.packageId == -999) {
//            containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
//            imageIV.setImageResource(R.mipmap.ic_setting)
//            imageIV.setIconDefaultsColor()
//        }
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
}