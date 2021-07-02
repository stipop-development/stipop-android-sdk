package io.stipop.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.Utils
import io.stipop.model.SPPackage
import kotlinx.android.synthetic.main.activity_store.*
import kotlin.math.roundToInt


class PopularStickerAdapter(private val dataList: ArrayList<SPPackage>, val context: Context):
    RecyclerView.Adapter<PopularStickerAdapter.ViewHolder>() {

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
            .inflate(R.layout.item_popular_sticker, viewGroup, false)

        val screenWidth = Utils.getScreenWidth(context)
        val itemWidth = (screenWidth - Utils.dpToPx(48F) - (Utils.dpToPx(7F) * 3)) / 4

        val itemHeight = (75 * itemWidth) / 73

        view.layoutParams = ViewGroup.LayoutParams(itemWidth.toInt(), itemHeight.toInt())

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        val packageImg = item.packageImg

        Glide.with(context).load(packageImg).into(holder.imageIV)

        val drawable = holder.backgroundLL.background as GradientDrawable
        val color = Color.parseColor(Config.themeGroupedContentBackgroundColor)
        drawable.setColor(color)

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