package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemMyActivePackageBinding
import io.stipop.refactor.domain.entities.SPPackageItem

class MyActivePackageAdapter :
    ListAdapter<SPPackageItem, MyActivePackageAdapter.MyActivePackageViewHolder>(
        object : DiffUtil.ItemCallback<SPPackageItem>() {
            override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean =
                oldItem.hashCode() == newItem.hashCode()
            override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    ) {

    class MyActivePackageViewHolder(
        val _binding: ItemMyActivePackageBinding,
    ) : RecyclerView.ViewHolder(_binding.root) {

        fun onBind(item: SPPackageItem) {
            _binding.let {
                Glide.with(it.packageImage)
                    .load(item.packageImg)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(it.packageImage)
                    .clearOnDetach()

                it.packageName.text = item.packageName
                it.artistName.text = item.artistName
            }
        }
    }

    var hiddenClick: ((SPPackageItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyActivePackageViewHolder {
        return MyActivePackageViewHolder(
            ItemMyActivePackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            _binding.apply {
                hiddenButton.setOnClickListener {
                    if (bindingAdapterPosition >= 0) {
                        hiddenClick?.invoke(getItem(bindingAdapterPosition))
                    }
                }
                moveButton.setOnDragListener { v, event ->
                    bindingAdapterPosition < 0
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MyActivePackageViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}


