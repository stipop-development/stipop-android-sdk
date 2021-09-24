package io.stipop.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import io.stipop.R
import io.stipop.view_store.AllStickerFragment

class RecentKeywordAdapter(var myContext: Context, var view: Int, var data: ArrayList<String>, var fragment: AllStickerFragment): ArrayAdapter<String>(myContext, view, data) {

    private lateinit var item: ViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var retView: View

        if (convertView == null) {
            retView = View.inflate(myContext, view, null)
            item = ViewHolder(retView)
            retView.tag = item
        } else {
            retView = convertView
            item = convertView.tag as ViewHolder
            if (item == null) {
                retView = View.inflate(myContext, view, null)
                item = ViewHolder(retView)
                retView.tag = item
            }
        }

        val keyword = data[position]

        item.keywordTV.text = keyword

        item.removeLL.setOnClickListener {
            fragment.deleteKeyword(keyword)
        }

        return retView
    }

    override fun getItem(position: Int): String {
        return data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.count()
    }

    class ViewHolder(v: View) {
        val keywordTV: TextView = v.findViewById(R.id.keywordTV) as TextView
        val removeLL: LinearLayout = v.findViewById(R.id.removeLL) as LinearLayout
    }

}