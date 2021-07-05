package io.stipop.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.adapter.StickerAdapter
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONObject
import java.io.IOException

class DetailActivity: Activity() {

    lateinit var context: Context

    lateinit var stickerAdapter: StickerAdapter

    var stickerData = ArrayList<SPSticker>()

    var packageId = -1

    var packageAnimated: String? = ""

    lateinit var spPackage:SPPackage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        this.context = this

        packageId = intent.getIntExtra("packageId", -1)


        val drawable = containerLL.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color

        contentsRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))

        packageNameTV.setTextColor(Config.getDetailPackageNameTextColor(context))

        backIV.setImageResource(Config.getBackIconResourceId(context))
        closeIV.setImageResource(Config.getCloseIconResourceId(context))


        backIV.setIconDefaultsColor()
        closeIV.setIconDefaultsColor()


        val drawable2 = downloadTV.background as GradientDrawable
        drawable2.setColor(Color.parseColor(Config.themeMainColor)) // solid  color

        stickerGV.numColumns = Config.detailNumOfColumns


        backLL.setOnClickListener { finish() }
        closeLL.setOnClickListener { finish() }

        downloadTV.setOnClickListener {
            if (downloadTV.tag as Boolean) {
                // Toast.makeText(context, "이미 다운로드한 스티커입니다!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            downloadPackage()
        }

        stickerAdapter = StickerAdapter(context, R.layout.item_sticker, stickerData)
        stickerGV.adapter = stickerAdapter

        getPackInfo()
    }

    private fun getPackInfo() {

        val params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(this, APIClient.APIPath.PACKAGE.rawValue + "/$packageId", params) { response: JSONObject?, e: IOException? ->
            println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")
                    val packageObj = body.getJSONObject("package")

                    val stickers = packageObj.getJSONArray("stickers")

                    for (i in 0 until stickers.length()) {
                        stickerData.add(SPSticker(stickers.get(i) as JSONObject))
                    }

                    this.spPackage = SPPackage(packageObj)

                    packageAnimated = this.spPackage.packageAnimated

                    Glide.with(context).load(this.spPackage.packageImg).into(packageIV)

                    packageNameTV.text = this.spPackage.packageName
                    artistNameTV.text = this.spPackage.artistName

                    if (this.spPackage.isDownload) {
                        downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)
                        downloadTV.text = "DOWNLOADED"
                    } else {
                        downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background)
                        downloadTV.text = "DOWNLOAD"
                    }

                    downloadTV.tag = this.spPackage.isDownload
                }

            } else {
                e?.printStackTrace()
            }

            stickerAdapter.notifyDataSetChanged()
        }

    }

    private fun downloadPackage() {

        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("isPurchase", Config.allowPremium)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)

        if (Config.allowPremium == "Y") {
            // 움직이지 않는 스티커
            var price = Config.pngPrice

            if (packageAnimated == "Y") {
                // 움직이는 스티커
                price = Config.gifPrice
            }
            params.put("price", price)
        }

        APIClient.post(this, APIClient.APIPath.DOWNLOAD.rawValue + "/$packageId", params) { response: JSONObject?, e: IOException? ->
            println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (Utils.getString(header, "status") == "success") {

                    val intent = Intent()
                    intent.putExtra("packageId", packageId)
                    setResult(RESULT_OK, intent)

                    // download
                    PackUtils.downloadAndSaveLocal(this, this.spPackage) {
                        downloadTV.text = "DOWNLOADED"
                        downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)

                        Toast.makeText(context, "다운로드 완료!", Toast.LENGTH_LONG).show()
                    }
                }

            } else {
                e?.printStackTrace()
            }
        }

    }

}