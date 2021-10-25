package io.stipop.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.Sticker
import io.stipop.viewholder.StickerThumbViewHolder

class GridStickerAdapter(private val dataSet: ArrayList<Sticker> = ArrayList()) :
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
}