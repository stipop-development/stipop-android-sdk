package io.stipop.refactor.present.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemMyActivePackageBinding
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.present.ui.listeners.OnHiddenPackageListener
import io.stipop.refactor.present.ui.listeners.OnMovePackageListener
import io.stipop.refactor.present.ui.listeners.OnStartDragListener

class MyActivePackageAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val _dataList: ArrayList<SPPackage> = arrayListOf()

    var onHiddenPackageListener: OnHiddenPackageListener? = null
    var onMovePackageListener: OnMovePackageListener? = null
    var onStartDragListener: OnStartDragListener? = null

    val itemTouchHelperCallback: ItemTouchHelper.Callback = object :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyActivePackageViewHolder(
            ItemMyActivePackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onHiddenPackageListener,
            onStartDragListener,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = _dataList[position]

        when (holder) {
            is MyActivePackageViewHolder -> {
                holder.setItem(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return _dataList.size
    }

    fun setItemList(itemList: List<SPPackage>) {
        _dataList.clear()
        _dataList.addAll(itemList)
        notifyDataSetChanged()
    }
}

class MyActivePackageViewHolder(
    private val _binding: ViewBinding,
    private val _onHiddenPackageListener: OnHiddenPackageListener?,
    private val _onStartDragListener: OnStartDragListener?,
) : RecyclerView.ViewHolder(_binding.root) {

    fun setItem(item: SPPackage) {
        when (_binding) {
            is ItemMyActivePackageBinding -> {
                _binding.let {
                    Glide.with(it.packageImage)
                        .load(item.packageImg)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(it.packageImage)
                        .clearOnDetach()

                    it.packageName.text = item.packageName
                    it.artistName.text = item.artistName
                    it.hiddenButton.setOnClickListener {
                        _onHiddenPackageListener?.onHidden(item)
                    }
                    it.moveButton.setOnDragListener { v, event ->
                        Log.d(this::class.simpleName, "setOnDragListener")
                        _onStartDragListener?.onStartDrag(this)
                        false
                    }
                }
            }
        }
    }
}
