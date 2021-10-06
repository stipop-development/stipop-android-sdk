package io.stipop.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.StickerPackage
import io.stipop.viewholder.HorizontalStickerThumbViewHolder

class HorizontalStickerThumbnailAdapter(private val dataSet: ArrayList<StickerPackage> = ArrayList()) :
    RecyclerView.Adapter<HorizontalStickerThumbViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalStickerThumbViewHolder {
        return HorizontalStickerThumbViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: HorizontalStickerThumbViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun updateData(datas: List<StickerPackage>) {
        val prevCount = dataSet.size
        dataSet.addAll(datas)
        notifyItemRangeInserted(prevCount, itemCount - 1)
    }
}