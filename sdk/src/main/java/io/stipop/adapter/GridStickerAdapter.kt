package io.stipop.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.SPSticker
import io.stipop.models.Sticker
import io.stipop.models.StickerPackage
import io.stipop.viewholder.StickerThumbViewHolder

internal class GridStickerAdapter(private val dataSet: ArrayList<SPSticker> = ArrayList()) :
    RecyclerView.Adapter<StickerThumbViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StickerThumbViewHolder {
        return StickerThumbViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StickerThumbViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateData(stickerPackage: StickerPackage) {
        dataSet.clear()
        notifyItemRangeRemoved(0, itemCount)
        dataSet.addAll(stickerPackage.stickers)
        notifyItemRangeInserted(0, itemCount)
    }
}