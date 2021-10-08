package io.stipop.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.AllStickerDataSource
import io.stipop.models.StickerPackage
import io.stipop.viewholder.HeaderViewHolder
import io.stipop.viewholder.HorizontalStickerThumbContainerViewHolder
import io.stipop.viewholder.VerticalStickerThumbViewHolder
import io.stipop.viewholder.delegates.VerticalStickerThumbViewHolderDelegate

class AllStickerAdapter(private val verticalStickerThumbViewHolderDelegate: VerticalStickerThumbViewHolderDelegate? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet = AllStickerDataSource()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TRENDING -> {
                HorizontalStickerThumbContainerViewHolder.create(parent)
            }
            TYPE_LIST_HEADER -> {
                HeaderViewHolder.create(parent)
            }
            else -> {
                VerticalStickerThumbViewHolder.create(
                    parent,
                    verticalStickerThumbViewHolderDelegate
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {
                (holder as HorizontalStickerThumbContainerViewHolder).bind(
                    "Trending",
                    dataSet.trendingList
                )
            }
            1 -> {
                (holder as HeaderViewHolder).bind("Result")
            }
            else -> {
                val alignedPosition = position - 2
                (holder as VerticalStickerThumbViewHolder).bind(dataSet.defaultList[alignedPosition])
            }
        }
    }

    fun updateData(stickerPackages: List<StickerPackage>) {
        val prevItemCount = itemCount
        dataSet.apply {
            if (trendingList.isEmpty()) {
                run loop@{
                    stickerPackages.forEachIndexed { _, stickerPackage ->
                        if (trendingList.size > 7) return@loop
                        trendingList.add(stickerPackage)
                    }
                }
                defaultList.addAll(stickerPackages.subList(8, stickerPackages.size - 1))
                notifyItemRangeInserted(0, itemCount - 1)
            } else {
                defaultList.addAll(stickerPackages)
                notifyItemRangeInserted(prevItemCount, itemCount - 1)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.defaultList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_TRENDING
            1 -> TYPE_LIST_HEADER
            else -> TYPE_LIST_ITEM
        }
    }

    fun updateDownloadState(packageId: Int) {
        var position = 0
        run loop@{
            dataSet.defaultList.forEachIndexed { index, stickerPackage ->
                if(stickerPackage.packageId == packageId){
                    position = index
                    stickerPackage.download = "Y"
                    dataSet.defaultList[position] = stickerPackage
                    return@loop
                }
            }
        }
        position += 2
        notifyItemChanged(position)
    }

    companion object {
        private const val TYPE_TRENDING = 1000
        private const val TYPE_LIST_HEADER = 1001
        private const val TYPE_LIST_ITEM = 1002
    }
}