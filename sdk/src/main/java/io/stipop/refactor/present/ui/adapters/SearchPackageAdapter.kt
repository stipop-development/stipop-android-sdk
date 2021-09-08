package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.databinding.ItemSearchPackageBinding
import io.stipop.refactor.data.models.SPPackage

class SearchPackageAdapter : RecyclerView.Adapter<SearchPackageViewHolder>() {

    private val _itemList: ArrayList<SPPackage> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPackageViewHolder {
        return SearchPackageViewHolder(
            ItemSearchPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchPackageViewHolder, position: Int) {
        holder.setItem(_itemList[position])
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

class SearchPackageViewHolder(private val _binding: ItemSearchPackageBinding) : RecyclerView.ViewHolder(_binding.root) {

    fun setItem(item: SPPackage) {
        _binding.packageName.text = item.packageName
        _binding.artistName.text = item.artistName

        item.stickers.let {
            it.forEachIndexed { index, sticker ->
                when (index) {
                    0 -> Glide.with(_binding.root).load(sticker.stickerImg).into(_binding.image1IV)
                    1 -> Glide.with(_binding.root).load(sticker.stickerImg).into(_binding.image2IV)
                    2 -> Glide.with(_binding.root).load(sticker.stickerImg).into(_binding.image3IV)
                    3 -> Glide.with(_binding.root).load(sticker.stickerImg).into(_binding.image4IV)
                    else -> return
                }
            }

        }
    }
}
