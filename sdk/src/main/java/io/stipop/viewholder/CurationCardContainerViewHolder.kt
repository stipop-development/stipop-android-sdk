package io.stipop.viewholder

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.Utils
import io.stipop.adapter.PackageHorizontalAdapter
import io.stipop.custom.HorizontalDecoration
import io.stipop.databinding.ItemHorizontalStickerThumbContainerBinding
import io.stipop.models.CuratedCard
import io.stipop.setStipopUnderlineColor
import io.stipop.viewholder.delegates.StickerPackageClickDelegate

internal class CurationCardContainerViewHolder(
    private val binding: ItemHorizontalStickerThumbContainerBinding,
    val delegate: StickerPackageClickDelegate?
) :
    RecyclerView.ViewHolder(binding.root) {

    private val horizontalAdapter: PackageHorizontalAdapter by lazy { PackageHorizontalAdapter(delegate = delegate) }
    private val decoration = HorizontalDecoration(Utils.dpToPx(12F).toInt(), 0, Utils.dpToPx(10F).toInt())

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
        with(binding) {
            titleTextView.text = curatedCard?.cardTitle ?: ""
            recyclerView.adapter = horizontalAdapter
            horizontalAdapter.run {
                clearData()
                updateData(curatedCard?.packageList ?: emptyList())
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: StickerPackageClickDelegate?
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
