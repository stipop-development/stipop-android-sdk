package io.stipop.adapter.legacy

import android.annotation.SuppressLint
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
import io.stipop.view.KeyboardPopup
import io.stipop.custom.StipopImageView
import io.stipop.models.SPPackage


class StickerPackageThumbnailAdapter(var keyboardPopup: KeyboardPopup) :
    RecyclerView.Adapter<StickerPackageThumbnailAdapter.StickerPackageThumbnailViewHolder>() {

    private val dataList: ArrayList<SPPackage> = ArrayList()

    interface OnPackageClickListener {
        fun onItemClick(position: Int, data: SPPackage)
    }

    private var mListener: OnPackageClickListener? = null

    fun setData(packages: ArrayList<SPPackage>) {
        val startPos = dataList.size
        dataList.addAll(packages)
        val endPos = dataList.size - 1
        notifyItemRangeInserted(startPos, endPos)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataList[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StickerPackageThumbnailViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_keyboard_package, viewGroup, false)
        return StickerPackageThumbnailViewHolder(view)
    }

    override fun onBindViewHolder(holderStickerPackageThumbnail: StickerPackageThumbnailViewHolder, position: Int) {
        holderStickerPackageThumbnail.bindData(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setOnItemClickListener(listener: OnPackageClickListener?) {
        mListener = listener
    }

    inner class StickerPackageThumbnailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIV: StipopImageView = view.findViewById(R.id.imageIV)
        val containerLL: LinearLayout = view.findViewById(R.id.containerLL)

        fun bindData(data: SPPackage){
            val item = data
            if (item.packageId == -999) {
                containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
                imageIV.setImageResource(R.mipmap.ic_setting)
                imageIV.setIconDefaultsColor()
            } else {
                val packageImg = item.packageImg

                Glide.with(itemView.context).load(packageImg).dontAnimate().into(imageIV)

                val matrix = ColorMatrix()

                if (keyboardPopup.selectedPackageId == item.packageId) {
                    containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
                    matrix.setSaturation(1.0f)
                } else {
                    containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
                    matrix.setSaturation(0.0f)
                }
                imageIV.colorFilter = ColorMatrixColorFilter(matrix)
            }

            containerLL.setOnClickListener {
                    mListener?.onItemClick(bindingAdapterPosition, item)
            }
        }
    }
}