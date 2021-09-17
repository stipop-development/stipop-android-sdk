package io.stipop.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.custom.StipopImageView
import io.stipop.view.AllStickerFragment
import io.stipop.models.SPPackage

class AllStickerAdapter(var myContext: Context, var view: Int, var data: ArrayList<SPPackage>, var allStickerFragment: AllStickerFragment): ArrayAdapter<SPPackage>(myContext, view, data) {

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

        val packageObj = data[position]

        item.packageNameTV.setTextColor(Config.getAllStickerPackageNameTextColor(myContext))
        item.artistNameTV.setTextColor(Config.getTitleTextColor(myContext))


        item.packageNameTV.text = packageObj.packageName
        item.artistNameTV.text = packageObj.artistName

        if (packageObj.isDownload) {
            item.downloadIV.setImageResource(Config.getCompleteIconResourceId(myContext))

            item.downloadIV.setIconDefaultsColor()
        } else {
            item.downloadIV.setImageResource(Config.getDownloadIconResourceId(myContext))

            item.downloadIV.setTint()
        }


        item.downloadIV.setOnClickListener {
            if (!packageObj.isDownload) {

                if (Stipop.instance!!.delegate.canDownload(packageObj)) {
                    allStickerFragment.getPackInfo(position, packageObj.packageId)
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
        return data[position]
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