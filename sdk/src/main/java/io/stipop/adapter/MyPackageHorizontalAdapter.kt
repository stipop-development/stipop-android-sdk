package io.stipop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.stipop.R
import io.stipop.models.StickerPackage
import io.stipop.viewholder.SpvThumbHolder

internal class MyPackageHorizontalAdapter(val delegate: OnPackageClickListener) :
    PagingDataAdapter<StickerPackage, SpvThumbHolder>(REPO_COMPARATOR) {

    interface OnPackageClickListener {
        fun onPackageClick(position: Int, stickerPackage: StickerPackage)
    }

    var prevSelectedPosition = -1

    fun getItemByPosition(position: Int): StickerPackage? {
        return if (itemCount > position)
            getItem(position)
        else
            null
    }

    fun isSelectedItemExist() = prevSelectedPosition != -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpvThumbHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keyboard_package, parent, false)
        return SpvThumbHolder(view, delegate)
    }

    override fun onBindViewHolder(holder: SpvThumbHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it, position == prevSelectedPosition)
        }
    }

    fun updateSelected(position: Int = -1) {
        if (prevSelectedPosition >= 0 && prevSelectedPosition != position) {
            notifyItemChanged(prevSelectedPosition, Unit)
        }
        prevSelectedPosition = position
        notifyItemChanged(prevSelectedPosition, Unit)
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<StickerPackage>() {
            override fun areItemsTheSame(
                oldItem: StickerPackage,
                newItem: StickerPackage
            ): Boolean =
                (oldItem.packageId == newItem.packageId && oldItem.getIsVisible() == newItem.getIsVisible())

            override fun areContentsTheSame(
                oldItem: StickerPackage,
                newItem: StickerPackage
            ): Boolean = oldItem == newItem
        }
    }
}
