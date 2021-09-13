package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemKeyboardPackageBinding
import io.stipop.refactor.domain.entities.SPPackageItem

class KeyboardPackageAdapter : ListAdapter<SPPackageItem, KeyboardPackageAdapter.KeyboardPackageViewHolder>(object :
    DiffUtil.ItemCallback<SPPackageItem>() {
    override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean = oldItem == newItem
}) {
    companion object {
        enum class Type(val rawValue: Int) {
            UNSELECTED_ITEM(0X00),
            SELECTED_ITEM(0X01);

            companion object {
                fun fromRawValue(rawValue: Int): Type {
                    return Type.values().first { it.rawValue == rawValue }
                }
            }
        }
    }

    class KeyboardPackageViewHolder(override val binding: ItemKeyboardPackageBinding) :
        ViewBindingAdapter.ViewBindingHolder<SPPackageItem>(
            binding
        ) {
        override fun onBind(item: SPPackageItem) {
            Glide.with(itemView)
                .load(item.packageImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.packageImageButton)
                .clearOnDetach()
        }
    }

    var itemClick: ((SPPackageItem) -> Unit)? = null
    internal var selectedItem: SPPackageItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyboardPackageViewHolder {
        return KeyboardPackageViewHolder(
            ItemKeyboardPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
            .apply {
                when (Type.fromRawValue(viewType)) {
                    Type.UNSELECTED_ITEM -> {
                        binding.root.isSelected = false
                    }
                    Type.SELECTED_ITEM -> {
                        binding.root.isSelected = true
                    }
                }
                binding.root.setOnClickListener {
                    itemClick?.invoke(getItem(absoluteAdapterPosition))
                }
            }
    }

    override fun onBindViewHolder(holder: KeyboardPackageViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (selectedItem == getItem(position)) {
            true -> Type.SELECTED_ITEM
            false -> Type.UNSELECTED_ITEM
        }.rawValue
    }
}
