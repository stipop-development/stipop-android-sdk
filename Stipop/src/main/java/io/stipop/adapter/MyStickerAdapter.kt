package io.stipop.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.extend.dragdrop.ItemTouchHelperViewHolder
import io.stipop.extend.dragdrop.OnRecyclerAdapterEventListener
import io.stipop.fragment.MyStickerFragment
import io.stipop.model.SPPackage
import java.util.*
import kotlin.collections.ArrayList


class MyStickerAdapter(private val context: Context, private val dataList: ArrayList<SPPackage>, var myStickerFragment: MyStickerFragment):
    RecyclerView.Adapter<MyStickerAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    var fromPosition = -1
    var toPosition = -1

    private var onEventListener: OnRecyclerAdapterEventListener? = null

    fun setOnRecyclerAdapterEventListener(l: OnRecyclerAdapterEventListener) {
        onEventListener = l
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val containerLL: LinearLayout

        val packageIV: ImageView
        val packageNameTV: TextView
        val artistNameTV: TextView

        val isViewLL: LinearLayout
        val moveLL: LinearLayout
        val hideLL: LinearLayout

        val addLL: LinearLayout

        init {
            containerLL = view.findViewById(R.id.containerLL)

            moveLL = view.findViewById(R.id.moveLL)
            packageIV = view.findViewById(R.id.packageIV)
            packageNameTV = view.findViewById(R.id.packageNameTV)
            artistNameTV = view.findViewById(R.id.artistNameTV)
            isViewLL = view.findViewById(R.id.isViewLL)
            hideLL = view.findViewById(R.id.hideLL)
            addLL = view.findViewById(R.id.addLL)
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_my_sticker, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spPackage = dataList[position]

        Glide.with(context).load(spPackage.packageImg).into(holder.packageIV)

        holder.artistNameTV.setText(spPackage.artistName)
        holder.packageNameTV.setText(spPackage.packageName)

        holder.isViewLL.visibility = View.GONE
        holder.addLL.visibility = View.GONE
        if (spPackage.isView) {
            holder.isViewLL.visibility = View.VISIBLE

            holder.hideLL.setOnClickListener {
                myStickerFragment.hidePackage(spPackage.packageId)
            }
        } else {
            holder.addLL.visibility = View.VISIBLE
        }

        holder.containerLL.setBackgroundColor(Color.WHITE)
        holder.moveLL.setOnTouchListener { _, event ->
            holder.containerLL.setBackgroundColor(ContextCompat.getColor(context, R.color.c_f7f8f9))
            if (event.action == MotionEvent.ACTION_DOWN) {
                onEventListener?.onDragStarted(holder)
            }
            return@setOnTouchListener false
        }
//        holder.moveLL.setOnClickListener {
//            myStickerFragment.myStickerOrder(0, 3)
//        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        swap(fromPosition, toPosition)
        return true
    }

    override fun onItemRemove(position: Int) {
        dataList.removeAt(position)
        notifyDataSetChanged()
    }

    private fun swap(from: Int, to: Int) {
        if (this.fromPosition == -1) {
            this.fromPosition = from
        }

        this.toPosition = to

        Collections.swap(dataList, from, to)
        notifyItemMoved(from, to)

    }

    override fun finishedDragAndDrop() {

        println("this.fromPosition: " + this.fromPosition)
        println("this.toPosition: " + this.toPosition)

        if (this.fromPosition < 0 || this.toPosition < 0 && this.fromPosition == this.toPosition) {
            this.fromPosition = -1
            this.toPosition = -1
            return
        }

         myStickerFragment.myStickerOrder(this.fromPosition, this.toPosition)

        println("dataList: " + dataList.toString())

        this.fromPosition = -1
        this.toPosition = -1

        notifyDataSetChanged()
    }

}