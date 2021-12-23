package io.stipop.adapter.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import io.stipop.Config
import io.stipop.R
import io.stipop.databinding.ItemChipKeywordsBinding
import io.stipop.models.response.KeywordListResponse
import io.stipop.event.KeywordClickDelegate

internal class KeywordTagViewHolder(
    private val binding: ItemChipKeywordsBinding,
    private val keywordClickDelegate: KeywordClickDelegate? = null
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            (group.findViewById<Chip>(checkedId)?.text)?.let {
                keywordClickDelegate?.onKeywordClicked(it.toString().trim())
            }
        }
    }

    fun bind(keywords: List<KeywordListResponse.KeywordSet>) {
        keywords.forEach {
            Chip(itemView.context).apply {
                text = it.keyword
                isCheckable = true
                isCheckedIconVisible = false
                chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(Config.themeMainColor))
                setTextColor(Color.WHITE)
            }.run {
                binding.chipGroup.addView(this)
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            keywordClickDelegate: KeywordClickDelegate? = null
        ): KeywordTagViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chip_keywords, parent, false)
            val binding = ItemChipKeywordsBinding.bind(view)
            return KeywordTagViewHolder(binding, keywordClickDelegate)
        }
    }
}