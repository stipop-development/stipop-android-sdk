package io.stipop.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.extend.StipopImageView
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

        holder.hideIV.setImageResource(Config.getHideIconResourceId(context))
        holder.moveIV.setImageResource(Config.getOrderIconResourceId(context))

        holder.addIV.setImageResource(Config.getAddIconResourceId())


        holder.hideIV.setIconDefaultsColor()
        holder.moveIV.setIconDefaultsColor()
        holder.addIV.setIconDefaultsColor()


        Glide.with(context).load(spPackage.packageImg).into(holder.packageIV)

        holder.artistNameTV.text = spPackage.artistName
        holder.packageNameTV.text = spPackage.packageName

        holder.isViewLL.visibility = View.GONE
        holder.addLL.visibility = View.GONE

        val matrix = ColorMatrix()

        if (spPackage.isView) {
            holder.packageNameTV.setTextColor(Config.getAllStickerPackageNameTextColor(context))
            holder.artistNameTV.setTextColor(Config.getTitleTextColor(context))

            holder.isViewLL.visibility = View.VISIBLE

            holder.hideLL.setOnClickListener {
                myStickerFragment.showConfirmAlert(spPackage.packageId, position)
            }

            matrix.setSaturation(1.0f)
        } else {
            holder.artistNameTV.setTextColor(Config.getMyStickerHiddenArtistNameTextColor(context))
            holder.packageNameTV.setTextColor(Config.getMyStickerHiddenPackageNameTextColor(context))

            holder.addLL.visibility = View.VISIBLE

            holder.addLL.setOnClickListener {
                myStickerFragment.hidePackage(spPackage.packageId, position)
            }

            matrix.setSaturation(0.0f)
        }

        holder.packageIV.colorFilter = ColorMatrixColorFilter(matrix)

//        holder.moveLL.setOnTouchListener { _, event ->
//            holder.containerLL.setBackgroundColor(ContextCompat.getColor(context, R.color.c_f7f8f9))
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                onEventListener?.onDragStarted(holder)
//            }
//            return@setOnTouchListener false
//        }

        holder.moveLL.setOnLongClickListener {
            holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))

            onEventListener?.onDragStarted(holder)

            return@setOnLongClickListener true
        }

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

        // Collections.swap(dataList, from, to)
        notifyItemMoved(from, to)

    }

    override fun finishedDragAndDrop() {

        if (this.fromPosition < 0 || this.toPosition < 0 && this.fromPosition == this.toPosition) {
            this.fromPosition = -1
            this.toPosition = -1
            return
        }

         myStickerFragment.myStickerOrder(this.fromPosition, this.toPosition)

        this.fromPosition = -1
        this.toPosition = -1
    }

}