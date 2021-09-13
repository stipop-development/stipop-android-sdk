package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemPackageBinding
import io.stipop.databinding.ItemStoreAllPackageBinding
import io.stipop.refactor.domain.entities.SPPackageItem

class StorePackageAdapter :
    ListAdapter<SPPackageItem, StorePackageAdapter.StorePackageHolder>(object : DiffUtil.ItemCallback<SPPackageItem>() {
        override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem.hashCode() == newItem.hashCode()
        override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem.hashCode() == newItem.hashCode()
    }) {

    companion object {
        const val TRENDING: Int = 0x0
        const val ALL: Int = 0x1
    }

    class StorePackageHolder constructor(
        override val binding: ItemStoreAllPackageBinding
    ) : ViewBindingAdapter.ViewBindingHolder<SPPackageItem>(binding) {
        override fun onBind(item: SPPackageItem) {
            Glide.with(itemView)
                .load(item.packageImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.packageImage)
                .clearOnDetach()

            binding.packageName.text = item.packageName
            binding.artistName.text = item.artistName

            binding.downloadButton.isEnabled = item.isDownload == "N"
        }
    }

    var itemClick: ((SPPackageItem) -> Unit)? = null
    var downloadClick: ((SPPackageItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorePackageHolder {
        return StorePackageHolder(
            ItemStoreAllPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
        ).apply {
            binding.root.setOnClickListener {
                itemClick?.invoke(getItem(bindingAdapterPosition))
            }
            binding.downloadButton.setOnClickListener {
                downloadClick?.invoke(getItem(bindingAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: StorePackageHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}


// 트랜딩을 위한
class StoreTrendingPackageAdapter :
    ListAdapter<SPPackageItem, StoreTrendingPackageAdapter.StoreTrendingPackageHolder>(object :
        DiffUtil.ItemCallback<SPPackageItem>() {
        override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
    }) {

    class StoreTrendingPackageHolder(override val binding: ItemPackageBinding) :
        ViewBindingAdapter.ViewBindingHolder<SPPackageItem>(binding) {
        override fun onBind(item: SPPackageItem) {
            Glide.with(itemView)
                .load(item.packageImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.packageImage)
                .clearOnDetach()
        }
    }

    var itemClick: ((SPPackageItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreTrendingPackageHolder {
        return StoreTrendingPackageHolder(ItemPackageBinding.inflate(LayoutInflater.from(parent.context))).apply {
            binding.root.setOnClickListener {
                itemClick?.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: StoreTrendingPackageHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
