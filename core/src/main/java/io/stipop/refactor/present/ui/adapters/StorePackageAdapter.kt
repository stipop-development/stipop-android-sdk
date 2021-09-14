package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemStoreAllPackageBinding
import io.stipop.databinding.ItemStoreTrendingPackageBinding
import io.stipop.databinding.LayoutStorePackageHeaderBinding
import io.stipop.databinding.LayoutTrendingPackageBinding
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
class StoreTrendingPackageItemListAdapter :
    ListAdapter<List<SPPackageItem>, StoreTrendingPackageItemListAdapter.StoreTrendingPackageHolder>(object :
        DiffUtil.ItemCallback<List<SPPackageItem>>() {
        override fun areItemsTheSame(oldItem: List<SPPackageItem>, newItem: List<SPPackageItem>): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: List<SPPackageItem>, newItem: List<SPPackageItem>): Boolean =
            oldItem == newItem
    }) {

    class StoreTrendingPackageHolder(override val binding: LayoutTrendingPackageBinding) :
        ViewBindingAdapter.ViewBindingHolder<List<SPPackageItem>>(binding) {

        var itemClick: ((SPPackageItem) -> Unit)? = null

        override fun onBind(item: List<SPPackageItem>) {
            binding.apply {
                trendingPackageList.apply {
                    layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
                    adapter = StoreTrendingPackageItemAdapter(itemClick).apply {
                        submitList(item)
                    }
                }
            }
        }
    }

    var itemClick: ((SPPackageItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreTrendingPackageHolder {
        return StoreTrendingPackageHolder(LayoutTrendingPackageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: StoreTrendingPackageHolder, position: Int) {
        holder.itemClick = itemClick
        holder.onBind(getItem(position))
    }
}

// 트랜딩 아이템을 위한
class StoreTrendingPackageItemAdapter
constructor(
    var itemClick: ((SPPackageItem) -> Unit)?
) :
    ListAdapter<SPPackageItem, StoreTrendingPackageItemAdapter.StoreTrendingPackageItemHolder>(object :
        DiffUtil.ItemCallback<SPPackageItem>() {
        override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
    }) {

    class StoreTrendingPackageItemHolder(override val binding: ItemStoreTrendingPackageBinding) :
        ViewBindingAdapter.ViewBindingHolder<SPPackageItem>(binding) {
        override fun onBind(item: SPPackageItem) {
            Glide.with(itemView)
                .load(item.packageImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.packageImage)
                .clearOnDetach()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreTrendingPackageItemHolder {
        return StoreTrendingPackageItemHolder(ItemStoreTrendingPackageBinding.inflate(LayoutInflater.from(parent.context))).apply {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition >= 0) {
                    itemClick?.invoke(getItem(bindingAdapterPosition))
                }
            }
        }
    }

    override fun onBindViewHolder(holder: StoreTrendingPackageItemHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}

class StorePackageHeaderAdapter(
    private val header: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        val binding: LayoutStorePackageHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutStorePackageHeaderBinding.inflate(LayoutInflater.from(parent.context)))
            .apply {
                binding.header.text = header
            }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 1

}
