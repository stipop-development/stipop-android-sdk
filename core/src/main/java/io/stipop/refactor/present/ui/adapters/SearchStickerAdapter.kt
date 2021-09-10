package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import io.stipop.databinding.ItemStickerBinding
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.present.ui.contracts.PagingContract


class SearchStickerAdapter() :
    RecyclerView.Adapter<SearchStickerAdapter.ViewHolder>(),
    PagingContract.View<SPStickerItem> {


    private lateinit var _binding: ItemStickerBinding
    private var _presenter: PagingContract.Presenter<SPStickerItem>? = null
    private var _itemList: List<SPStickerItem> = arrayListOf()

    class ViewHolder(private val _binding: ViewBinding) : RecyclerView.ViewHolder(_binding.root) {

        fun onBind(item: SPStickerItem) {
            when (_binding) {
                is ItemStickerBinding -> {
                    Glide.with(_binding.stickerImage).load(item.stickerImg).into(_binding.stickerImage)
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        _binding = ItemStickerBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(_binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.onBind(item)
        holder.itemView.setOnClickListener {
            onClickItem(item)
        }
        notifyCurrentPosition(position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override val presenter: PagingContract.Presenter<SPStickerItem>?
        get() = _presenter
    override val itemList: List<SPStickerItem>
        get() = _itemList

    override fun onBind(presenter: PagingContract.Presenter<SPStickerItem>?) {
        _presenter = presenter
    }

    override fun notifyCurrentPosition(index: Int) {
        _presenter?.onLoadMoreList(index)
    }

    override fun setItemList(itemList: List<SPStickerItem>) {
        _itemList = itemList
        notifyDataSetChanged()
    }

    override fun onClickItem(item: SPStickerItem) {
        _presenter?.onClickedItem(item)
    }

}
