package io.stipop.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.Utils
import org.json.JSONObject


class KeywordAdapter(private val dataList: ArrayList<JSONObject>):
    RecyclerView.Adapter<KeywordAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val keywordTV: TextView = view.findViewById(R.id.keywordTV)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_keyword, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        val drawable = holder.keywordTV.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.themeMainColor))

        holder.keywordTV.text = Utils.getString(item, "keyword")
        holder.keywordTV.setOnClickListener {
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