package io.stipop.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginRight
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.Utils
import io.stipop.model.SPPackage

class AllStickerAdapter(var myContext: Context, var view: Int, var data: ArrayList<SPPackage>): ArrayAdapter<SPPackage>(myContext, view, data) {

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

        val packageObj = data.get(position)

        item.packageNameTV.setTextColor(Config.getAllStickerPackageNameTextColor(myContext))
        item.artistNameTV.setTextColor(Config.getAllStickerArtistNameTextColor(myContext))


        item.packageNameTV.setText(packageObj.packageName)
        item.artistNameTV.setText(packageObj.artistName)

        if (packageObj.isDownload) {
            item.downloadIV.setImageResource(Config.getCompleteIconResourceId(myContext))
        } else {
            item.downloadIV.setImageResource(Config.getDownloadIconResourceId(myContext))
        }

        if (Config.storeListType == "singular") {
            Glide.with(myContext).load(packageObj.packageImg).into(item.packageIV!!)
        } else {
            item.stickersLL?.removeAllViews()

            for (i in 0 until packageObj.stickers.size) {
                val stickerObj = packageObj.stickers[i]

                val layoutParams = ViewGroup.MarginLayoutParams(Utils.dpToPx(55f).toInt(), Utils.dpToPx(55f).toInt())
                layoutParams.rightMargin = Utils.dpToPx(8f).toInt()

                val iv = ImageView(myContext)
                iv.layoutParams = layoutParams

                Glide.with(myContext).load(stickerObj.stickerImg).into(iv)

                item.stickersLL?.addView(iv)
            }
        }

        return retView
    }

    override fun getItem(position: Int): SPPackage {
        return data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.count()
    }

    class ViewHolder(v: View) {
        val packageIV: ImageView? = v.findViewById(R.id.packageIV) as ImageView?
        val packageNameTV: TextView = v.findViewById(R.id.packageNameTV) as TextView
        val artistNameTV: TextView = v.findViewById(R.id.artistNameTV) as TextView
        val downloadIV: ImageView = v.findViewById(R.id.downloadIV) as ImageView

        val stickersLL: LinearLayout? = v.findViewById(R.id.stickersLL) as LinearLayout?
    }

}