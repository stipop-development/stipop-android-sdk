package io.stipop.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.StickerPackage
import io.stipop.viewholder.HorizontalStickerThumbContainerViewHolder
import io.stipop.viewholder.delegates.StickerPackageClickDelegate

internal class HomeAdapter(private val stickerPackageClickDelegate: StickerPackageClickDelegate? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var screenType: ScreenType = ScreenType.DEFAULT
    private var dataSet: ArrayList<Any> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HorizontalStickerThumbContainerViewHolder.create(parent, stickerPackageClickDelegate)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataSet[position] as? List<StickerPackage>
        when(position){
            0->{
                (holder as HorizontalStickerThumbContainerViewHolder).bind("MD`s Pick", data ?: emptyList())
            }
            1->{
                (holder as HorizontalStickerThumbContainerViewHolder).bind("Weekly`s Pack", data ?: emptyList())
            }
            2->{
                (holder as HorizontalStickerThumbContainerViewHolder).bind("Hot Popular", data ?: emptyList())
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setInitData(sets: ArrayList<List<StickerPackage>>) {
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