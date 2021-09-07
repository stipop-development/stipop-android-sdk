package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import io.stipop.databinding.ItemStickerBinding
import io.stipop.refactor.data.models.SPSticker

class StoreDetailPackageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val _itemList: ArrayList<SPSticker> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StoreDetailPackageViewHolder(
            ItemStickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val _item = _itemList[position]

        when (holder) {
            is StoreDetailPackageViewHolder -> {
                holder.setItem(_item)
            }
        }
    }

    override fun getItemCount(): Int {
        return _itemList.size
    }

    fun setItemList(itemList: List<SPSticker>) {
        _itemList.clear()
        _itemList.addAll(itemList)
        notifyDataSetChanged()
    }
}

class StoreDetailPackageViewHolder(private val _binding: ViewBinding) : RecyclerView.ViewHolder(_binding.root) {

    fun setItem(item: SPSticker) {
        when (_binding) {
            is ItemStickerBinding -> {
                _binding.stickerImage.apply {
                    Glide.with(this).load(item.stickerImg).into(this)
                }
            }
        }
    }

}
