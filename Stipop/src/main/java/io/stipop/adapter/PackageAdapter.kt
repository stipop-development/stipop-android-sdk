package io.stipop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.R
import io.stipop.Utils
import io.stipop.model.SPPackage
import org.json.JSONObject


class PackageAdapter(private val dataList: ArrayList<SPPackage>, val context: Context):
    RecyclerView.Adapter<PackageAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageIV: ImageView
        val backgroundLL: LinearLayout

        init {
            imageIV = view.findViewById(R.id.imageIV)
            backgroundLL = view.findViewById(R.id.backgroundLL)
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_package, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        val packageImg = item.packageImg

        Glide.with(context).load(packageImg).into(holder.imageIV)

        holder.backgroundLL.setOnClickListener {
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