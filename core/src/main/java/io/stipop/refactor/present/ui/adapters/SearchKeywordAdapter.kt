package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.stipop.databinding.ItemKeywordBinding
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.present.ui.contracts.PagingContract


class SearchKeywordAdapter() :
    RecyclerView.Adapter<SearchKeywordAdapter.ViewHolder>(),
    PagingContract.View<SPKeywordItem> {


    private lateinit var _binding: ItemKeywordBinding
    private var _presenter: PagingContract.Presenter<SPKeywordItem>? = null
    private var _itemList: List<SPKeywordItem> = arrayListOf()

    class ViewHolder(private val _binding: ViewBinding) : RecyclerView.ViewHolder(_binding.root) {

        fun onBind(item: SPKeywordItem) {
            when (_binding) {
                is ItemKeywordBinding -> {
                    _binding.keywordTV.text = item.keyword
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        _binding = ItemKeywordBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
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

    override val presenter: PagingContract.Presenter<SPKeywordItem>?
        get() = _presenter
    override val itemList: List<SPKeywordItem>
        get() = _itemList

    override fun onBind(presenter: PagingContract.Presenter<SPKeywordItem>?) {
        _presenter = presenter
    }

    override fun notifyCurrentPosition(index: Int) {
        _presenter?.onLoadMoreList(index)
    }

    override fun setItemList(itemList: List<SPKeywordItem>) {
        _itemList = itemList
        notifyDataSetChanged()
    }

    override fun onClickItem(item: SPKeywordItem) {
        _presenter?.onClickedItem(item)
    }

}
