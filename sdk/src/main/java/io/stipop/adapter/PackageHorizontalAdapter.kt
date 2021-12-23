package io.stipop.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.CuratedCard
import io.stipop.models.StickerPackage
import io.stipop.adapter.viewholder.CurationAtypeViewHolder
import io.stipop.adapter.viewholder.CurationBtypeViewHolder
import io.stipop.adapter.viewholder.HorizontalStickerThumbViewHolder
import io.stipop.event.PackClickDelegate

internal class PackageHorizontalAdapter(
    private val dataSet: ArrayList<StickerPackage> = ArrayList(),
    val delegate: PackClickDelegate?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var curatedCard: CuratedCard? = null

    companion object {
        const val TYPE_DEFAULT = 1000
        const val TYPE_A = 1001
        const val TYPE_B = 1002
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_A -> {
                CurationAtypeViewHolder.create(parent, delegate)
            }
            TYPE_B -> {
                CurationBtypeViewHolder.create(parent, delegate)
            }
            else -> {
                HorizontalStickerThumbViewHolder.create(parent, delegate)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_A -> {
                (holder as CurationAtypeViewHolder).bind(dataSet[position])
            }
            TYPE_B -> {
                (holder as CurationBtypeViewHolder).bind(dataSet[position])
            }
            else -> {
                (holder as HorizontalStickerThumbViewHolder).bind(dataSet[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        curatedCard?.let {
            return when (it.type) {
                "A" -> {
                    TYPE_A
                }
                "B" -> {
                    TYPE_B
                }
                else -> {
                    TYPE_DEFAULT
                }
            }
        }
        return TYPE_DEFAULT
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun clearData() {
        val prevCount = itemCount
        dataSet.clear()
        notifyItemRangeRemoved(0, prevCount - 1)
    }

    fun updateData(datas: List<StickerPackage>) {
        val prevCount = dataSet.size
        dataSet.addAll(datas)
        notifyItemRangeInserted(prevCount, itemCount - 1)
    }
}