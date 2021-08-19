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
import io.stipop.databinding.ActivityDetailBinding
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import org.json.JSONObject
import java.io.IOException

class DetailActivity : Activity() {

    lateinit var _binding: ActivityDetailBinding
    lateinit var _context: Context

    lateinit var stickerAdapter: StickerAdapter

    var stickerData = ArrayList<SPSticker>()

    var packageId = -1

    var packageAnimated: String? = ""

    lateinit var spPackage: SPPackage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _context = this

        packageId = intent.getIntExtra("packageId", -1)

        val drawable = _binding.containerLL.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color

        _binding.contentsRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))

        _binding.packageNameTV.setTextColor(Config.getDetailPackageNameTextColor(_context))

        _binding.backIV.setImageResource(Config.getBackIconResourceId(_context))
        _binding.closeIV.setImageResource(Config.getCloseIconResourceId(_context))


        _binding.backIV.setIconDefaultsColor()
        _binding.closeIV.setIconDefaultsColor()


        _binding.stickerGV.numColumns = Config.detailNumOfColumns


        _binding.backLL.setOnClickListener { finish() }
        _binding.closeLL.setOnClickListener { finish() }

        _binding.downloadTV.setOnClickListener {
            if (_binding.downloadTV.tag as Boolean) {
                return@setOnClickListener
            }

            if (Stipop.instance!!.delegate.canDownload(this.spPackage)) {
                downloadPackage()
            } else {
                Utils.alert(this, getString(R.string.can_not_download))
            }

        }

        stickerAdapter = StickerAdapter(_context, R.layout.item_sticker, stickerData)
        _binding.stickerGV.adapter = stickerAdapter

        getPackInfo()
    }

    private fun getPackInfo() {

        val params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(
            this,
            APIClient.APIPath.PACKAGE.rawValue + "/$packageId",
            params
        ) { response: JSONObject?, e: IOException? ->
            // println(response)

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

                    Glide.with(_context).load(this.spPackage.packageImg).into(_binding.packageIV)

                    _binding.packageNameTV.text = this.spPackage.packageName
                    _binding.artistNameTV.text = this.spPackage.artistName

                    if (this.spPackage.isDownload) {
                        _binding.downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)
                        _binding.downloadTV.text = getString(R.string.downloaded)
                    } else {
                        _binding.downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background)
                        _binding.downloadTV.text = getString(R.string.download)

                        val drawable2 = _binding.downloadTV.background as GradientDrawable
                        drawable2.setColor(Color.parseColor(Config.themeMainColor)) // solid  color
                    }

                    _binding.downloadTV.tag = this.spPackage.isDownload
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

        APIClient.post(
            this,
            APIClient.APIPath.DOWNLOAD.rawValue + "/$packageId",
            params
        ) { response: JSONObject?, e: IOException? ->
            // println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (Utils.getString(header, "status") == "success") {

                    val intent = Intent()
                    intent.putExtra("packageId", packageId)
                    setResult(RESULT_OK, intent)

                    // download
                    PackUtils.downloadAndSaveLocal(this, this.spPackage) {
                        _binding.downloadTV.text = getString(R.string.downloaded)
                        _binding.downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)

                        Toast.makeText(
                            _context,
                            getString(R.string.download_done),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } else {
                e?.printStackTrace()
            }
        }

    }

}