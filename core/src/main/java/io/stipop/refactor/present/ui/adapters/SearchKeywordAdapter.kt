package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import io.stipop.databinding.ItemKeywordBinding
import io.stipop.refactor.domain.entities.SPKeywordItem

class SearchKeywordAdapter() :
    ViewBindingAdapter<SPKeywordItem>() {

    class SearchKeywordViewHolder(override val binding: ItemKeywordBinding) :
        ViewBindingHolder<SPKeywordItem>(binding) {
        override fun onBind(item: SPKeywordItem) {
            binding.keyword.text = item.keyword
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<SPKeywordItem> {
        return SearchKeywordViewHolder(ItemKeywordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}
