package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemMyHiddenPackageBinding
import io.stipop.extend.dragdrop.ItemTouchHelperAdapter
import io.stipop.extend.dragdrop.OnRecyclerAdapterEventListener
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.present.ui.listeners.OnActivePackageItemListener

class MyHiddenPackageAdapter :
    ListAdapter<SPPackageItem, MyHiddenPackageViewHolder>(
        object : DiffUtil.ItemCallback<SPPackageItem>() {
            override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean =
                oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    ), ItemTouchHelperAdapter {

    var fromPosition = -1
    var toPosition = -1

    private var onEventListener: OnRecyclerAdapterEventListener? = null

    fun setOnRecyclerAdapterEventListener(l: OnRecyclerAdapterEventListener) {
        onEventListener = l
    }

    var onActivePackageListener: OnActivePackageItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHiddenPackageViewHolder {
        return MyHiddenPackageViewHolder(
            ItemMyHiddenPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onActivePackageListener
        )
    }

    override fun onBindViewHolder(holder: MyHiddenPackageViewHolder, position: Int) {
        holder.onBind(getItem(position))


    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        swap(fromPosition, toPosition)
        return true
    }

    override fun onItemRemove(position: Int) {
        notifyItemRemoved(position)
    }

    private fun swap(from: Int, to: Int) {
        if (this.fromPosition == -1) {
            this.fromPosition = from
        }

        this.toPosition = to

        // Collections.swap(dataList, from, to)
        notifyItemMoved(from, to)

    }

    override fun finishedDragAndDrop() {

        if (this.fromPosition < 0 || this.toPosition < 0 && this.fromPosition == this.toPosition) {
            this.fromPosition = -1
            this.toPosition = -1
            return
        }

        this.fromPosition = -1
        this.toPosition = -1
    }
}

class MyHiddenPackageViewHolder(
    private val _binding: ViewBinding,
    private val _onActivePackageListener: OnActivePackageItemListener?
) : RecyclerView.ViewHolder(_binding.root) {

    fun onBind(item: SPPackageItem) {
        when (_binding) {
            is ItemMyHiddenPackageBinding -> {
                _binding.run {
                    Glide.with(packageImage)
                        .load(item.packageImg)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(packageImage)
                        .clearOnDetach()

                    packageName.text = item.packageName
                    artistName.text = item.artistName

                    activeButton.setOnClickListener {
                        _onActivePackageListener?.onActive(item)
                    }
                }
            }
        }
    }
}
