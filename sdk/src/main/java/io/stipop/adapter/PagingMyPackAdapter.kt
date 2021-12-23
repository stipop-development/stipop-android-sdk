package io.stipop.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.stipop.custom.DragAndDropDelegate
import io.stipop.event.MyPackEventDelegate
import io.stipop.models.StickerPackage
import io.stipop.adapter.viewholder.MyPackFullWidthViewHolder
import io.stipop.adapter.viewholder.MyPackThumbViewHolder

internal class PagingMyPackAdapter(private val type: ViewType, private val delegate: MyPackEventDelegate) : PagingDataAdapter<StickerPackage, RecyclerView.ViewHolder>(REPO_COMPARATOR), DragAndDropDelegate {

    enum class ViewType(val typeNum: Int) {
        SPV(1000), STORE(1001), ERROR(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ViewType.SPV.typeNum->{
                MyPackThumbViewHolder.create(parent, delegate)
            }
            else->{
                MyPackFullWidthViewHolder.create(parent, delegate)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val repoItem = getItem(position)
        when(type){
            ViewType.SPV -> {
                (holder as MyPackThumbViewHolder).bindData(repoItem, position == prevSelectedPosition)
            }
            ViewType.STORE -> {
                (holder as MyPackFullWidthViewHolder).bind(repoItem)
            }
            else -> {
                //
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(type){
            ViewType.SPV -> ViewType.SPV.typeNum
            ViewType.STORE -> ViewType.STORE.typeNum
            else -> ViewType.ERROR.typeNum
        }
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
            ): Boolean =
                oldItem == newItem
        }
    }

    fun isSelectedItemExist() = prevSelectedPosition != -1
    var prevSelectedPosition = -1
    var fromData: StickerPackage? = null
    var toData: StickerPackage? = null

    fun getItemByPosition(position: Int): StickerPackage? {
        return if (itemCount > position)
            getItem(position)
        else
            null
    }

    fun updateSelected(position: Int = -1) {
        if (prevSelectedPosition >= 0 && prevSelectedPosition != position) {
            notifyItemChanged(prevSelectedPosition, Unit)
        }
        prevSelectedPosition = position
        notifyItemChanged(prevSelectedPosition, Unit)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromData == null) {
            fromData = getItem(fromPosition)
        }
        toData = getItem(toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemRemove(position: Int) {
        notifyItemRemoved(position)
    }

    override fun onItemMoveCompleted() {
        if (fromData != null && toData != null && fromData != toData) {
            delegate.onDragCompleted(fromData!!, toData!!)
            fromData = null
            toData = null
        }
    }
}
