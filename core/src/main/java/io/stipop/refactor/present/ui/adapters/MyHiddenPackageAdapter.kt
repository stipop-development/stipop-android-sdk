package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.extend.dragdrop.ItemTouchHelperAdapter
import io.stipop.databinding.ItemMyHiddenPackageBinding
import io.stipop.extend.dragdrop.OnRecyclerAdapterEventListener
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.present.ui.listeners.OnActivePackageListener

class MyHiddenPackageAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    private val _dataList: ArrayList<SPPackage> = arrayListOf()

    var fromPosition = -1
    var toPosition = -1

    private var onEventListener: OnRecyclerAdapterEventListener? = null

    fun setOnRecyclerAdapterEventListener(l: OnRecyclerAdapterEventListener) {
        onEventListener = l
    }

    var onActivePackageListener: OnActivePackageListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyHiddenPackageViewHolder(
            ItemMyHiddenPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onActivePackageListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = _dataList[position]

        when (holder) {
            is MyHiddenPackageViewHolder -> {
                holder.setItem(item)
            }
        }

    }

    override fun getItemCount(): Int {
        return _dataList.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        swap(fromPosition, toPosition)
        return true
    }

    override fun onItemRemove(position: Int) {
        _dataList.removeAt(position)
        notifyDataSetChanged()
    }

    private fun swap(from: Int, to: Int) {
        if (this.fromPosition == -1) {
            this.fromPosition = from
        }

        this.toPosition = to

        // Collections.swap(dataList, from, to)
        notifyItemMoved(from, to)

    }

    override fun finishedDragAndDrop() {

        if (this.fromPosition < 0 || this.toPosition < 0 && this.fromPosition == this.toPosition) {
            this.fromPosition = -1
            this.toPosition = -1
            return
        }

//         myPackageFragment.myStickerOrder(this.fromPosition, this.toPosition)

        this.fromPosition = -1
        this.toPosition = -1
    }

    fun setItemList(itemList: List<SPPackage>) {
        _dataList.clear()
        _dataList.addAll(itemList)
        notifyDataSetChanged()
    }

}

class MyHiddenPackageViewHolder(
    private val _binding: ViewBinding,
    private val _onActivePackageListener: OnActivePackageListener?
) : RecyclerView.ViewHolder(_binding.root) {

    fun setItem(item: SPPackage) {
        when (_binding) {
            is ItemMyHiddenPackageBinding -> {
                _binding.run {
                    Glide.with(packageImage)
                        .load(item.packageImg)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(packageImage)
                        .clearOnDetach()

                    packageName.text = item.packageName
                    artistName.text = item.artistName

                    activeButton.setOnClickListener {
                        _onActivePackageListener?.onActive(item)
                    }
                }
            }
        }
    }
}
