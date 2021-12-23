package io.stipop.adapter.viewholder

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.StipopUtils
import io.stipop.adapter.PackageHorizontalAdapter
import io.stipop.custom.HorizontalDecoration
import io.stipop.databinding.ItemHorizontalStickerThumbContainerBinding
import io.stipop.models.CuratedCard
import io.stipop.setStipopUnderlineColor
import io.stipop.event.PackClickDelegate

internal class CurationCardContainerViewHolder(
    private val binding: ItemHorizontalStickerThumbContainerBinding,
    val delegate: PackClickDelegate?
) :
    RecyclerView.ViewHolder(binding.root) {

    private val horizontalAdapter: PackageHorizontalAdapter by lazy { PackageHorizontalAdapter(delegate = delegate) }
    private val decoration = HorizontalDecoration(StipopUtils.dpToPx(12F).toInt(), 0, StipopUtils.dpToPx(10F).toInt())

    init {
        with(binding) {
            underLine.setStipopUnderlineColor()
            titleTextView.setTextColor(Config.getTitleTextColor(itemView.context))
            recyclerView.apply {
                removeItemDecoration(decoration)
                addItemDecoration(decoration)
                addOnItemTouchListener(listener)
            }
        }
    }

    fun bind(curatedCard: CuratedCard?) {
        horizontalAdapter.curatedCard = curatedCard
        binding.container.isVisible = curatedCard != null
        curatedCard?.let { card ->
            binding.titleTextView.text = card.cardTitle
            binding.recyclerView.adapter = horizontalAdapter
            horizontalAdapter.run {
                clearData()
                updateData(card.packageList)
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: PackClickDelegate?
        ): CurationCardContainerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal_sticker_thumb_container, parent, false)
            val binding = ItemHorizontalStickerThumbContainerBinding.bind(view)
            return CurationCardContainerViewHolder(binding, delegate)
        }

        val listener = object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.parent.requestDisallowInterceptTouchEvent(true)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                //
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                //
            }
        }
    }
}
