package io.stipop.refactor.present.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.databinding.ItemMyActivePackageBinding
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.present.ui.listeners.OnHiddenPackageListener
import io.stipop.refactor.present.ui.listeners.OnMovePackageListener
import io.stipop.refactor.present.ui.listeners.OnStartDragListener

class MyActivePackageAdapter :
    ListAdapter<SPPackageItem, MyActivePackageAdapter.MyActivePackageViewHolder>(
        object : DiffUtil.ItemCallback<SPPackageItem>() {
            override fun areItemsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean =
                oldItem.hashCode() == newItem.hashCode()
            override fun areContentsTheSame(oldItem: SPPackageItem, newItem: SPPackageItem): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    ) {

    class MyActivePackageViewHolder(
        private val _binding: ItemMyActivePackageBinding,
        private val _onHiddenPackageListener: OnHiddenPackageListener?,
        private val _onStartDragListener: OnStartDragListener?,
    ) : RecyclerView.ViewHolder(_binding.root) {

        fun onBind(item: SPPackageItem) {
            _binding.let {
                Glide.with(it.packageImage)
                    .load(item.packageImg)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(it.packageImage)
                    .clearOnDetach()

                it.packageName.text = item.packageName
                it.artistName.text = item.artistName
                it.hiddenButton.setOnClickListener {
                }
                it.moveButton.setOnDragListener { v, event ->
                    Log.d(this::class.simpleName, "setOnDragListener")
                    _onStartDragListener?.onStartDrag(this)
                    false
                }
            }
        }
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyActivePackageViewHolder {
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

    override fun onBindViewHolder(holder: MyActivePackageViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}


