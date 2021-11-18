package io.stipop

import android.app.Activity
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


internal object StipopUtils {

    fun downloadAtLocal(stickerPackage: StickerPackage, responseCallback: () -> Unit) {
        val stickers = stickerPackage.stickers
        for (sticker in stickers) {
            val packageId = sticker.packageId
            val stickerImg = sticker.stickerImg
            downloadImage(packageId, stickerImg, sticker)
        }
        responseCallback()
    }

    private fun downloadImage(packageId: Int, encodedString: String?, sticker: SPSticker) {
        if (encodedString == null) {
            return
        }
        val fileName = encodedString.split(File.separator).last()
        var filePath = File(Stipop.applicationContext.filesDir, "stipop/$packageId/$fileName")
        if (filePath.isDirectory) {
            filePath.delete()
        }
        filePath = File(Stipop.applicationContext.filesDir, "stipop/$packageId")
        filePath.mkdirs()
        filePath = File(Stipop.applicationContext.filesDir, "stipop/$packageId/$fileName")

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        URL(encodedString).openStream().use { input ->
            FileOutputStream(filePath).use { output ->
                input.copyTo(output)
            }
            saveStickerAsJson(Stipop.applicationContext, sticker, packageId)
        }
    }

    fun getStickersFromLocal(activity: Activity, packageId: Int): ArrayList<SPSticker> {
        val stickerList = ArrayList<SPSticker>()

        val filePath = File(activity.filesDir, "stipop/$packageId")
        if (filePath.exists()) {
            filePath.walkTopDown().forEach {
                if (it.isFile) {
                    val fileName = it.name

                    if (fileName.contains(".json")) {
                        return@forEach
                    }

                    val sticker = SPSticker()

                    val fileNames = fileName.split(".")
                    if (fileNames.count() > 0) {
                        val jsonFileName = fileNames[0]

                        val file =
                            File(activity.filesDir, "stipop/$packageId/$jsonFileName.json")
                        if (file.isFile) {
                            val json = JSONObject(file.readText())
                            sticker.stickerId = Utils.getInt(json, "stickerId")
                            sticker.stickerImg = Utils.getString(json, "stickerImg")
                            sticker.favoriteYN = Utils.getString(json, "favoriteYN")
                        }
                    }

                    sticker.packageId = packageId
                    sticker.stickerImgLocalFilePath = it.absolutePath

                    stickerList.add(sticker)
                }
            }
        }

        return stickerList
    }

    fun saveStickerAsJson(context: Context, sticker: SPSticker, packageId: Int) {

        val fileName = sticker.stickerImg!!.split(File.separator).last()

        val fileNames = fileName.split(".")

        var jsonFileName = fileName
        if (fileNames.count() > 0) {
            jsonFileName = fileNames[0]
        }

        val filePath = File(context.filesDir, "stipop/$packageId/$jsonFileName.json")

        val json = JSONObject()
        json.put("stickerId", sticker.stickerId)
        json.put("stickerImg", sticker.stickerImg)
        json.put("favoriteYN", sticker.favoriteYN)
        json.put("keyword", sticker.keyword)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        filePath.writeText(json.toString())

    }

}