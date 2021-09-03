package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import io.stipop.R
import io.stipop.extend.StipopImageView
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.present.ui.components.common.SPPaging


class KeyboardStickerAdapter :
    RecyclerView.Adapter<KeyboardStickerAdapter.ViewHolder>(), SPPaging.View<SPStickerItem> {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var _presenter: SPPaging.Presenter<SPStickerItem>? = null
    private var _itemList: List<SPStickerItem> = listOf()
    private var mListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIV: StipopImageView = view.findViewById(R.id.sticker_image)
        val containerLL: LinearLayout = view.findViewById(R.id.containerLL)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_keyboard_package, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList[position]
        notifyCurrentPosition(position)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override val presenter: SPPaging.Presenter<SPStickerItem>?
        get() = _presenter
    override val itemList: List<SPStickerItem>
        get() = _itemList

    override fun onBind(presenter: SPPaging.Presenter<SPStickerItem>?) {
        _presenter = presenter
    }

    override fun notifyCurrentPosition(index: Int) {
        _presenter?.onLoadMoreList(index)
    }

    override fun setItemList(itemList: List<SPStickerItem>) {
        _itemList = itemList
        notifyDataSetChanged()
    }

}
