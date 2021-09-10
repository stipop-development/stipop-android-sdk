package io.stipop.refactor.present.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.stipop.refactor.present.ui.listeners.OnItemSelectListener


abstract class ListAdapter<T, B : ViewBinding> : RecyclerView.Adapter<ListAdapter.ListViewHolder<T, B>>() {
    abstract class ListViewHolder<T, B : ViewBinding>(open val binding: B) : RecyclerView.ViewHolder(binding.root) {
        abstract fun onBind(item: T)
    }

    lateinit var binding: B

    var itemList: List<T> = listOf()

    var itemSelectListener: OnItemSelectListener<T>? = null

    override fun onBindViewHolder(holder: ListViewHolder<T, B>, position: Int) {
        val item = itemList[position]
        holder.onBind(item)
        holder.itemView.setOnClickListener {
            itemSelectListener?.onSelect(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
