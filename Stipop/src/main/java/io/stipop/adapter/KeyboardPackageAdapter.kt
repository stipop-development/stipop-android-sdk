package io.stipop.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.activity.Keyboard
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage


class KeyboardPackageAdapter(private val dataList: ArrayList<SPPackage>, var context: Context, var keyboard: Keyboard):
    RecyclerView.Adapter<KeyboardPackageAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIV: StipopImageView = view.findViewById(R.id.imageIV)
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
        val item = dataList[position]

        if (item.packageId == -999) {
            // Settings
            holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            holder.imageIV.setImageResource(R.mipmap.ic_setting)
            holder.imageIV.setIconDefaultsColor()
        } else {
            val packageImg = item.packageImg

            Glide.with(context).load(packageImg).into(holder.imageIV)

            val matrix = ColorMatrix()

            if (keyboard.selectedPackageId == item.packageId) {
                holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
                matrix.setSaturation(1.0f)
            } else {
                holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
                matrix.setSaturation(0.0f)
            }

            holder.imageIV.colorFilter = ColorMatrixColorFilter(matrix)
        }

        holder.containerLL.setOnClickListener {
            if (mListener != null) {
                mListener!!.onItemClick(position)
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

}