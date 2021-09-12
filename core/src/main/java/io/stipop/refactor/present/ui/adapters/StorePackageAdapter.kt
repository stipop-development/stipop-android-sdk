package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import io.stipop.databinding.ItemPackageBinding
import io.stipop.databinding.ItemStoreAllPackageBinding
import io.stipop.databinding.ItemStoreTrendingPackageListBinding
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.present.ui.listeners.OnClickPackageListener
import io.stipop.refactor.present.ui.listeners.OnDownloadPackageListener

class StorePackageHolder constructor(
    override val binding: ViewBinding
) : ViewBindingAdapter.ViewBindingHolder<SPPackageItem>(binding) {
    override fun onBind(item: SPPackageItem) {
        when (binding) {
            is ItemStoreTrendingPackageListBinding -> {
                binding.apply {
                    trendingPackageList.layoutManager =
                        LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
                    trendingPackageList.adapter = StoreTrendingPackageAdapter().apply {
                    }
                }
            }
            is ItemStoreAllPackageBinding -> {

                Glide.with(itemView)
                    .load(item.packageImg)
                    .into(binding.packageImage)
                    .clearOnDetach()

                binding.packageName.text = item.packageName
                binding.artistName.text = item.artistName
            }
        }
    }
}

class StorePackageAdapter : ViewBindingAdapter<SPPackageItem>() {

    companion object {
        const val TRENDING: Int = 0x0
        const val ALL: Int = 0x1
    }

    var onClickPackageListener: OnClickPackageListener? = null
    var onDownloadPackageListener: OnDownloadPackageListener? = null

    private val _itemList: ArrayList<SPPackage> = arrayListOf()

    private val _trendingItemList: List<SPPackage>
        get() {
            return _itemList.filterIndexed { index, spPackage -> index < 12 }
        }

    private val _trendingItemCount: Int
        get() {
            return if (_trendingItemList.isEmpty()) {
                0
            } else {
                1
            }
        }

    private val _allItemList: List<SPPackage>
        get() {
            return _itemList.filterIndexed { index, spPackage -> index >= 12 }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<SPPackageItem> {
        return when (viewType) {
            TRENDING -> {
                StorePackageHolder(
                    ItemStoreTrendingPackageListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ALL -> {
                StorePackageHolder(
                    ItemStoreAllPackageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                )
            }
            else -> {
                throw Error(
                    "Incorrect view type : " +
                            "viewType -> $viewType"
                )
            }
        }

    }

    override fun onBindViewHolder(holder: ViewBindingHolder<SPPackageItem>, position: Int) {
        val item = itemList[position]
        holder.onBind(item)
    }
}

private class StoreTrendingPackageAdapter : ViewBindingAdapter<SPPackageItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<SPPackageItem> {
        return StoreTrendingPackageHolder(ItemPackageBinding.inflate(LayoutInflater.from(parent.context)))
    }

}

private class StoreTrendingPackageHolder(override val binding: ItemPackageBinding) :
    ViewBindingAdapter.ViewBindingHolder<SPPackageItem>(binding) {
    override fun onBind(item: SPPackageItem) {
        binding.apply {

        }
    }

}
