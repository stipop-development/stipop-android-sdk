package io.stipop.adapter.legacy

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.custom.StipopImageView
import io.stipop.models.SPPackage


internal class PackageAdapter(private val dataList: ArrayList<SPPackage>, val context: Context):
    RecyclerView.Adapter<PackageAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIV: StipopImageView = view.findViewById(R.id.imageIV)
        val backgroundLL: LinearLayout = view.findViewById(R.id.backgroundLL)
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

        val drawable = holder.backgroundLL.background as GradientDrawable
        Config.setStoreTrendingBackground(context, drawable)

        // holder.backgroundLL.alpha = Config.storeTrendingOpacity.toFloat()

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