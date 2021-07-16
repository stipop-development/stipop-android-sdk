package io.stipop.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import io.stipop.R
import io.stipop.extend.StipopImageView
import io.stipop.model.SPSticker


class StickerAdapter(context: Context, var view: Int, var data: ArrayList<SPSticker>): ArrayAdapter<SPSticker>(context, view, data) {

    private lateinit var item: ViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var retView: View

        if (convertView == null) {
            retView = View.inflate(context, view, null)
            item = ViewHolder(retView)
            retView.tag = item
        } else {
            retView = convertView
            item = convertView.tag as ViewHolder
            if (item == null) {
                retView = View.inflate(context, view, null)
                item = ViewHolder(retView)
                retView.tag = item
            }
        }

        val sticker = data[position]

        Glide.with(context).load(sticker.stickerImg).into(item.imageIV)

        return retView
    }

    override fun getItem(position: Int): SPSticker {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.count()
    }

    class ViewHolder(v: View) {
        val imageIV: StipopImageView = v.findViewById(R.id.imageIV) as StipopImageView
    }

}