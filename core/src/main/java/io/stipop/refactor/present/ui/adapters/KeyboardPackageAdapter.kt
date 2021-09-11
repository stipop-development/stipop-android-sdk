package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemKeyboardPackageBinding
import io.stipop.refactor.domain.entities.SPPackageItem

class KeyboardPackageAdapter :
    ViewBindingAdapter<SPPackageItem, ItemKeyboardPackageBinding>() {

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

    var selectedItem: SPPackageItem? = null

    class KeyboardPackageViewHolder(override val binding: ItemKeyboardPackageBinding) :
        ViewBindingAdapter.ViewBindingHolder<SPPackageItem, ItemKeyboardPackageBinding>(
            binding
        ) {
        override fun onBind(item: SPPackageItem) {
            Glide.with(itemView).load(item.packageImg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.packageImageButton).clearOnDetach()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (selectedItem == itemList[position]) {
            true -> Type.SELECTED_ITEM
            false -> Type.UNSELECTED_ITEM
        }.rawValue
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<SPPackageItem, ItemKeyboardPackageBinding> {
        return KeyboardPackageViewHolder(
            ItemKeyboardPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                when (Type.fromRawValue(viewType)) {
                    Type.UNSELECTED_ITEM -> {
                        root.isSelected = false
                    }
                    Type.SELECTED_ITEM -> {
                        root.isSelected = true
                    }
                }
            }
        )
    }
}
