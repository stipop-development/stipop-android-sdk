package io.stipop.adapter.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.databinding.ItemLoadStateFooterViewBinding

internal class LoadStateViewHolder(
    private val binding: ItemLoadStateFooterViewBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.progressBar.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(Config.themeMainColor))
        }
    }
    fun bind(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_load_state_footer_view, parent, false)
            val binding = ItemLoadStateFooterViewBinding.bind(view)
            return LoadStateViewHolder(binding, retry)
        }
    }
}