package io.stipop.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.CuratedCard
import io.stipop.models.response.KeywordListResponse
import io.stipop.viewholder.CurationCardContainerViewHolder
import io.stipop.viewholder.KeywordTagViewHolder
import io.stipop.viewholder.delegates.KeywordClickDelegate
import io.stipop.viewholder.delegates.StickerPackageClickDelegate

internal class HomeTabAdapter(
    private val stickerPackageClickDelegate: StickerPackageClickDelegate? = null,
    private val keywordClickDelegate: KeywordClickDelegate? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var screenType: ScreenType = ScreenType.DEFAULT
    private var dataSet: ArrayList<Any> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_RECOMMEND_KEYWORD -> {
                KeywordTagViewHolder.create(parent, keywordClickDelegate)
            }
            else -> {
                CurationCardContainerViewHolder.create(
                    parent,
                    stickerPackageClickDelegate
                )
            }
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
    fun setInitData(sets: ArrayList<Any>) {
        dataSet.clear()
        notifyDataSetChanged()
        dataSet.addAll(sets)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (screenType) {
            ScreenType.DEFAULT -> {
                when (position) {
                    0 -> TYPE_RECOMMEND_KEYWORD
                    1 -> TYPE_HORIZONTAL_THUMB
                    2 -> TYPE_HORIZONTAL_BANNER
                    else -> TYPE_LIST
                }
            }
            ScreenType.SEARCH_RESULT -> {
                TYPE_LIST
            }
        }
    }

    companion object {
        private const val TYPE_RECOMMEND_KEYWORD = 1000
        private const val TYPE_HORIZONTAL_THUMB = 1001
        private const val TYPE_HORIZONTAL_BANNER = 1002
        private const val TYPE_LIST = 1003
    }

    enum class ScreenType {
        DEFAULT, SEARCH_RESULT
    }
}