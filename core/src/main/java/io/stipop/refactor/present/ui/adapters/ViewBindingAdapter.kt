package io.stipop.refactor.present.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.stipop.refactor.present.ui.listeners.OnItemBindListener
import io.stipop.refactor.present.ui.listeners.OnItemSelectListener


abstract class ViewBindingAdapter<T, B : ViewBinding> : RecyclerView.Adapter<ViewBindingAdapter.ViewBindingHolder<T, B>>() {
    abstract class ViewBindingHolder<T, B : ViewBinding>(open val binding: B) : RecyclerView.ViewHolder(binding.root) {
        abstract fun onBind(item: T)
    }

    lateinit var binding: B

    var itemList: List<T> = listOf()

    var onItemSelectListener: OnItemSelectListener<T>? = null
    var onItemBindListener: OnItemBindListener<T>? = null

    override fun onBindViewHolder(holder: ViewBindingHolder<T, B>, position: Int) {
        val item = itemList[position]
        holder.onBind(item)
        holder.itemView.setOnClickListener {
            onItemSelectListener?.onSelect(item)
        }
        onItemBindListener?.onBind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
