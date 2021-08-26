package io.stipop.adapter.store.storePage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import io.stipop.databinding.ItemStoreAllPackageBinding
import io.stipop.databinding.ItemStoreTrendingPackageBinding
import io.stipop.databinding.ItemStoreTrendingPackageListBinding
import io.stipop.model.SPPackage

class StoreAllPackageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TRENDING: Int = 0x0
        const val ALL: Int = 0x1
    }

    var selectPackageCallback: SelectPackageCallback? = null
    var downloadPackageCallback: DownloadPackageCallback? = null

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TRENDING -> {
                StoreTrendingPackageListViewHolder(
                    ItemStoreTrendingPackageListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), selectPackageCallback
                )
            }
            ALL -> {
                StoreAllPackageViewHolder(
                    ItemStoreAllPackageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    selectPackageCallback,
                    downloadPackageCallback,
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StoreTrendingPackageListViewHolder -> {
                holder.setItemList(_trendingItemList)
            }
            is StoreAllPackageViewHolder -> {
                val _item = _allItemList[position - _trendingItemCount]
                holder.setItem(_item)
            }
        }
    }

    override fun getItemCount(): Int {
        return _allItemList.size + _trendingItemCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < 1) {
            TRENDING
        } else {
            ALL
        }
    }

    fun setItemList(itemList: List<SPPackage>) {
        _itemList.clear()
        _itemList.addAll(itemList)
        notifyDataSetChanged()
    }
}

class StoreAllPackageViewHolder(
    private val _binding: ViewBinding,
    private val _selectPackageCallback: SelectPackageCallback?,
    private val _downloadPackageCallback: DownloadPackageCallback?
) :
    RecyclerView.ViewHolder(_binding.root) {

    fun setItem(item: SPPackage) {
        when (_binding) {
            is ItemStoreAllPackageBinding -> {
                Glide.with(itemView).load(item.packageImg).into(_binding.packageImage)
                _binding.packageName.text = item.packageName
                _binding.artistName.text = item.artistName
                _binding.root.setOnClickListener {
                    _selectPackageCallback?.onSelect(item)
                }

                _binding.downloadButton.setOnClickListener {
                    _downloadPackageCallback?.onDownload(item)
                }
                _binding.downloadButton.isEnabled = !item.isDownload
            }
        }
    }
}

class StoreTrendingPackageListViewHolder(
    private val _binding: ViewBinding,
    private val _selectPackageCallback: SelectPackageCallback?
) :
    RecyclerView.ViewHolder(_binding.root) {

    fun setItemList(_trendingItemList: List<SPPackage>) {
        when (_binding) {
            is ItemStoreTrendingPackageListBinding -> {
                with(_binding) {
                    trendingPackageList.apply {
                        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        adapter = StoreTrendingPackageAdapter().apply {
                            setItemList(_trendingItemList)
                            selectPackageCallback = _selectPackageCallback
                        }
                    }
                }
            }
        }
    }
}

class StoreTrendingPackageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val _itemList: ArrayList<SPPackage> = arrayListOf()

    var selectPackageCallback: SelectPackageCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StoreTrendingViewHolder(
            ItemStoreTrendingPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val _item = _itemList[position]
        when (holder) {
            is StoreTrendingViewHolder -> {
                holder.setItem(_item)
                holder.itemView.setOnClickListener({
                    selectPackageCallback?.onSelect(_item)
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return _itemList.size
    }

    fun setItemList(itemList: List<SPPackage>) {
        _itemList.clear()
        _itemList.addAll(itemList)
        notifyDataSetChanged()
    }

}


class StoreTrendingViewHolder(private val _binding: ViewBinding) : RecyclerView.ViewHolder(_binding.root) {
    private var _item: SPPackage? = null

    fun setItem(item: SPPackage) {
        _item = item
        when (_binding) {
            is ItemStoreTrendingPackageBinding -> {
                with(_binding) {
                    packageImage.apply {
                        Glide.with(context).load(item.packageImg).into(this)
                    }
                }
            }
        }
    }
}

interface DownloadPackageCallback {
    fun onDownload(item: SPPackage)
}

interface SelectPackageCallback {
    fun onSelect(item: SPPackage)
}
