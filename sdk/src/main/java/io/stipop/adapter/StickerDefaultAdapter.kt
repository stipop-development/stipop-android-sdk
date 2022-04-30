package io.stipop.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.SPSticker
import io.stipop.models.Sticker
import io.stipop.models.StickerPackage
import io.stipop.adapter.viewholder.StickerThumbViewHolder

internal class StickerDefaultAdapter(
    val delegate: OnStickerClickListener? = null,
    private val dataSet: ArrayList<SPSticker> = ArrayList()
) :
    RecyclerView.Adapter<StickerThumbViewHolder>() {

    interface OnStickerClickListener {
        fun onStickerClick(position: Int, spSticker: SPSticker)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StickerThumbViewHolder {
        return StickerThumbViewHolder.create(parent, delegate)
    }

    override fun onBindViewHolder(holder: StickerThumbViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    fun updateData(stickerPackage: StickerPackage) {
        val prevCount = itemCount
        dataSet.addAll(stickerPackage.stickers)
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun updateData(stickers: List<SPSticker>) {
        val prevCount = itemCount
        dataSet.addAll(stickers)
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun updateData(sticker: Sticker) {
        val prevCount = itemCount
        dataSet.add(sticker.toSPSticker())
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun updateFavorite(updatedSticker: SPSticker): SPSticker? {
        var target: SPSticker? = null
        run loop@{
            dataSet.forEachIndexed { index, spSticker ->
                if (spSticker.stickerId == updatedSticker.stickerId) {
                    spSticker.favoriteYN = updatedSticker.favoriteYN
                    dataSet[index] = spSticker
                    notifyItemChanged(index)
                    target = spSticker
                    return@loop
                }
            }
        }
        return target
    }


}