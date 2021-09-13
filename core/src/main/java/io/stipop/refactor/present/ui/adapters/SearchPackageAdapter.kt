package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import io.stipop.databinding.ItemSearchPackageBinding
import io.stipop.refactor.domain.entities.SPPackageItem

class SearchPackageAdapter : ListAdapter<SPPackageItem, SearchPackageAdapter.SearchPackageViewHolder>(
    object : DiffUtil.ItemCallback<SPPackageItem>() {
        override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
    }
) {

    class SearchPackageViewHolder(val _binding: ItemSearchPackageBinding) : RecyclerView.ViewHolder(_binding.root) {

        fun setItem(item: SPPackageItem) {
            _binding.packageName.text = item.packageName
            _binding.artistName.text = item.artistName

            _binding.downloadButton.isEnabled = item.isDownload == "N"

            item.stickers.let {
                it.forEachIndexed { index, sticker ->
                    when (index) {
                        0 -> Glide.with(itemView)
                            .load(sticker.stickerImg)
                            .transition(withCrossFade())
                            .into(_binding.image1IV)
                            .clearOnDetach()

                        1 -> Glide.with(itemView)
                            .load(sticker.stickerImg)
                            .transition(withCrossFade())
                            .into(_binding.image2IV)
                            .clearOnDetach()

                        2 -> Glide.with(itemView)
                            .load(sticker.stickerImg)
                            .transition(withCrossFade())
                            .into(_binding.image3IV)
                            .clearOnDetach()

                        3 -> Glide.with(itemView)
                            .load(sticker.stickerImg)
                            .transition(withCrossFade())
                            .into(_binding.image4IV)
                            .clearOnDetach()
                        else -> return
                    }
                }

            }
        }
    }

    var downloadClick: ((SPPackageItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPackageViewHolder {
        return SearchPackageViewHolder(
            ItemSearchPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            _binding.downloadButton.setOnClickListener {
                downloadClick?.invoke(getItem(bindingAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: SearchPackageViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }
}


