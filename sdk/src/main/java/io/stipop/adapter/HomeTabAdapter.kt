package io.stipop.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.CuratedCard
import io.stipop.models.response.KeywordListResponse
import io.stipop.adapter.viewholder.CurationCardContainerViewHolder
import io.stipop.adapter.viewholder.KeywordTagViewHolder
import io.stipop.event.KeywordClickDelegate
import io.stipop.event.PackClickDelegate

internal class HomeTabAdapter(
    private val packClickDelegate: PackClickDelegate? = null,
    private val keywordClickDelegate: KeywordClickDelegate? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSet: ArrayList<Any?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_RECOMMEND_KEYWORD -> {
                KeywordTagViewHolder.create(parent, keywordClickDelegate)
            }
            else -> {
                CurationCardContainerViewHolder.create(parent, packClickDelegate)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_RECOMMEND_KEYWORD
            1 -> dataSet[position]?.let { TYPE_HORIZONTAL_THUMB } ?: run { TYPE_HORIZONTAL_BANNER }
            2 -> TYPE_HORIZONTAL_BANNER
            else -> TYPE_LIST
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {
                val data = dataSet[position] as? List<KeywordListResponse.KeywordSet>
                (holder as KeywordTagViewHolder).bind(data ?: emptyList())
            }
            1, 2 -> {
                val data = dataSet[position] as? CuratedCard
                (holder as CurationCardContainerViewHolder).bind(data)
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setInitData(sets: ArrayList<Any?>) {
        val filter = sets.filterNotNull()
        dataSet.clear()
        notifyDataSetChanged()
        dataSet.addAll(filter)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        val filter = dataSet.filterNotNull()
        return filter.size
    }

    companion object {
        private const val TYPE_RECOMMEND_KEYWORD = 1000
        private const val TYPE_HORIZONTAL_THUMB = 1001
        private const val TYPE_HORIZONTAL_BANNER = 1002
        private const val TYPE_LIST = 1003
    }
}