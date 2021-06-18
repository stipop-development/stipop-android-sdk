package io.stipop.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        this.context = this

        packageId = intent.getIntExtra("packageId", -1)

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

    fun getPackInfo() {

        var params = JSONObject()
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

                    val pack = SPPackage(packageObj)

                    Glide.with(context).load(pack.packageImg).into(packageIV)

                    packageNameTV.setText(pack.packageName)
                    artistNameTV.setText(pack.artistName)

                    if (pack.isDownload) {
                        downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)
                        downloadTV.setText("DOWNLOADED")
                    } else {
                        downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background)
                        downloadTV.setText("DOWNLOAD")
                    }

                    downloadTV.tag = pack.isDownload

                }

            } else {

            }

            stickerAdapter.notifyDataSetChanged()
        }

    }

    fun downloadPackage() {

        var params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("isPurchase", Config.allowPremium)

        APIClient.post(this, APIClient.APIPath.DOWNLOAD.rawValue + "/$packageId", params) { response: JSONObject?, e: IOException? ->
            println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (Utils.getString(header, "status") == "success") {
                    Toast.makeText(context, "다운로드 완료!", Toast.LENGTH_LONG).show()

                    downloadTV.setText("DOWNLOADED")
                    downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)

                    val intent = Intent()
                    intent.putExtra("packageId", packageId)
                    setResult(RESULT_OK, intent)
                }

            } else {

            }
        }

    }

}