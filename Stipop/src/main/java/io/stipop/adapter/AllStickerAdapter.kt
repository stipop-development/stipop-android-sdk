package io.stipop.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginRight
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONObject
import java.io.IOException

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
        }

        val packageObj = data.get(position)

        item.packageNameTV.setTextColor(Config.getAllStickerPackageNameTextColor(myContext))
        item.artistNameTV.setTextColor(Config.getTitleTextColor(myContext))


        item.packageNameTV.text = packageObj.packageName
        item.artistNameTV.text = packageObj.artistName

        if (packageObj.isDownload) {
            item.downloadIV.setImageResource(Config.getCompleteIconResourceId(myContext))
        } else {
            item.downloadIV.setImageResource(Config.getDownloadIconResourceId(myContext))
        }


        item.downloadIV.setOnClickListener {
            if (!packageObj.isDownload) {

                if (Stipop.instance!!.delegate.canDownload(packageObj)) {
                    val intent = Intent()
                    intent.action = "SET_DOWNLOAD"
                    intent.putExtra("idx", position)
                    intent.putExtra("package_id", packageObj.packageId)
                    context.sendBroadcast(intent)
                } else {
                    Utils.alert(context, "Can not download!!!")
                }
            }
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

    fun setDownload(position: Int) {
        data[position].download = "Y"
    }

    class ViewHolder(v: View) {
        val packageIV: StipopImageView? = v.findViewById(R.id.packageIV) as StipopImageView?
        val packageNameTV: TextView = v.findViewById(R.id.packageNameTV) as TextView
        val artistNameTV: TextView = v.findViewById(R.id.artistNameTV) as TextView
        val downloadIV: StipopImageView = v.findViewById(R.id.downloadIV) as StipopImageView

        val stickersLL: LinearLayout? = v.findViewById(R.id.stickersLL) as LinearLayout?
    }

}