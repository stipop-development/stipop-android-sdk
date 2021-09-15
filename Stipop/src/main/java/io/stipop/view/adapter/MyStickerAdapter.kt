package io.stipop.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.extend.StipopImageView
import io.stipop.extend.dragdrop.OnItemHolderEventListener
import io.stipop.view.MyStickerFragment
import io.stipop.model.SPPackage
import java.util.*
import kotlin.collections.ArrayList


class MyStickerAdapter(var myStickerFragment: MyStickerFragment) :
    RecyclerView.Adapter<MyStickerAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    private val dataList: ArrayList<SPPackage> = ArrayList()
    var fromPosition = -1
    var toPosition = -1


    private var onEventListener: OnItemHolderEventListener? = null

    fun getData(): ArrayList<SPPackage> = dataList

    fun setOnRecyclerAdapterEventListener(l: OnItemHolderEventListener) {
        onEventListener = l
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(dataSet: ArrayList<SPPackage>) {
        dataList.clear()
        dataList.addAll(dataSet)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val containerLL: LinearLayout = view.findViewById(R.id.containerLL)

        val packageIV: StipopImageView = view.findViewById(R.id.packageIV)
        val packageNameTV: TextView = view.findViewById(R.id.packageNameTV)
        val artistNameTV: TextView = view.findViewById(R.id.artistNameTV)

        val isViewLL: LinearLayout = view.findViewById(R.id.isViewLL)
        val moveLL: LinearLayout = view.findViewById(R.id.moveLL)
        val hideLL: LinearLayout = view.findViewById(R.id.hideLL)

        val moveIV: StipopImageView = view.findViewById(R.id.moveIV)
        val hideIV: StipopImageView = view.findViewById(R.id.hideIV)

        val addLL: LinearLayout = view.findViewById(R.id.addLL)

        val addIV: StipopImageView = view.findViewById(R.id.addIV)
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

        holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))

        holder.hideIV.setImageResource(Config.getHideIconResourceId(holder.itemView.context))
        holder.moveIV.setImageResource(Config.getOrderIconResourceId(holder.itemView.context))

        holder.addIV.setImageResource(Config.getAddIconResourceId())


        holder.hideIV.setIconDefaultsColor()
        holder.moveIV.setIconDefaultsColor()
        holder.addIV.setIconDefaultsColor()


        Glide.with(holder.itemView.context).load(spPackage.packageImg).into(holder.packageIV)

        holder.artistNameTV.text = spPackage.artistName
        holder.packageNameTV.text = spPackage.packageName

        holder.isViewLL.visibility = View.GONE
        holder.addLL.visibility = View.GONE

        val matrix = ColorMatrix()

        if (spPackage.isView) {
            holder.packageNameTV.setTextColor(Config.getAllStickerPackageNameTextColor(holder.itemView.context))
            holder.artistNameTV.setTextColor(Config.getTitleTextColor(holder.itemView.context))

            holder.isViewLL.visibility = View.VISIBLE

            holder.hideLL.setOnClickListener {
                myStickerFragment.showConfirmAlert(spPackage.packageId, position)
            }

            matrix.setSaturation(1.0f)
        } else {
            holder.artistNameTV.setTextColor(Config.getMyStickerHiddenArtistNameTextColor(holder.itemView.context))
            holder.packageNameTV.setTextColor(Config.getMyStickerHiddenPackageNameTextColor(holder.itemView.context))

            holder.addLL.visibility = View.VISIBLE

            holder.addLL.setOnClickListener {
                myStickerFragment.hidePackage(spPackage.packageId, position)
            }

            matrix.setSaturation(0.0f)
        }

        holder.packageIV.colorFilter = ColorMatrixColorFilter(matrix)

        holder.moveLL.setOnClickListener {
            holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            onEventListener?.onDragStarted(holder)
        }
        holder.moveLL.setOnLongClickListener {
            holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            onEventListener?.onDragStarted(holder)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        this.fromPosition = fromPosition
        this.toPosition = toPosition
        Collections.swap(dataList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemRemove(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMoveCompleted() {
        myStickerFragment.myStickerOrder(dataList[toPosition], dataList[fromPosition])
    }

}