package io.stipop.refactor.present.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import io.stipop.databinding.ItemKeyboardPackageBinding
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.present.ui.components.common.SPPaging


class KeyboardPackageAdapter :
    RecyclerView.Adapter<KeyboardPackageAdapter.ViewHolder>(), SPPaging.View<SPPackageItem> {

    private lateinit var _binding: ItemKeyboardPackageBinding
    private var _presenter: SPPaging.Presenter<SPPackageItem>? = null
    private var _itemList: List<SPPackageItem> = listOf()

    class ViewHolder(private val _binding: ViewBinding) : RecyclerView.ViewHolder(_binding.root) {

        fun onBind(item: SPPackageItem) {
            when (_binding) {
                is ItemKeyboardPackageBinding -> {
                    Glide.with(_binding.stickerImage).load(item.packageImg).into(_binding.stickerImage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemKeyboardPackageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(_binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = _itemList[position]
        holder.onBind(item)
        holder.itemView.setOnClickListener {
            onClickItem(item)
        }
        notifyCurrentPosition(position)
    }

    override fun getItemCount(): Int {
        return _itemList.size
    }

    override fun onBind(presenter: SPPaging.Presenter<SPPackageItem>?) {
        _presenter = presenter
    }

    override fun notifyCurrentPosition(index: Int) {
        _presenter?.onLoadMoreList(index)
    }

    override fun setItemList(itemList: List<SPPackageItem>) {
        Log.d(this::class.simpleName, "setItemList : \n" +
                "itemList.size -> ${itemList.size}")
        _itemList = itemList
        notifyDataSetChanged()
    }

    override val itemList: List<SPPackageItem>
        get() = _itemList

    override val presenter: SPPaging.Presenter<SPPackageItem>?
        get() = _presenter

    override fun onClickItem(item: SPPackageItem) {
        presenter?.onClickedItem(item)
    }

}
