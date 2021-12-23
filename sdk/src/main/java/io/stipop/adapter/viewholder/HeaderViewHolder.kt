package io.stipop.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.databinding.ItemHeaderTitleBinding

internal class HeaderViewHolder(private val binding: ItemHeaderTitleBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(title: String){
        with(binding){
            titleTextView.text = title
            titleTextView.setTextColor(Config.getTitleTextColor(itemView.context))
        }
    }

    companion object {
        fun create(parent: ViewGroup): HeaderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header_title, parent, false)
            val binding = ItemHeaderTitleBinding.bind(view)
            return HeaderViewHolder(binding)
        }
    }
}