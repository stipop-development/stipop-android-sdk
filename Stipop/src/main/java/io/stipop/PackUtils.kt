package io.stipop

import android.app.Activity
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL


public class PackUtils {

    companion object {
        fun downloadAndSaveLocal(activity:Activity, spPackage: SPPackage?, responseCallback: () -> Unit) {
            val stickers = spPackage!!.stickers

            println(stickers)

            for (sticker in stickers) {
                val packageId = sticker.packageId
                val stickerImg = sticker.stickerImg

                // val encodedString = URLEncoder.encode(stickerImg, "utf-8")

                downloadImage(activity, packageId, stickerImg, sticker)
            }

            responseCallback()
        }

        private fun downloadImage(activity:Activity, packageId: Int, encodedString: String?, sticker: SPSticker) {
            if (encodedString == null) {
                return
            }

            val fileName = encodedString.split(File.separator).last()
            var filePath = File(activity.filesDir, "stipop/$packageId/$fileName")
            if (filePath.isDirectory) {
                filePath.delete()
            }
            filePath = File(activity.filesDir, "stipop/$packageId")
            filePath.mkdirs()
            filePath = File(activity.filesDir, "stipop/$packageId/$fileName")

            println("filePath : $filePath")

            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            URL(encodedString).openStream().use { input ->
                FileOutputStream(filePath).use { output ->
                    input.copyTo(output)
                }

                saveStickerJsonData(activity, sticker, packageId, fileName)
            }
        }

        fun stickerListOf(activity:Activity, packageId:Int): ArrayList<SPSticker> {
            val stickerList = ArrayList<SPSticker>()

            val filePath = File(activity.filesDir, "stipop/$packageId")
            if (filePath.exists()) {
                filePath.walkTopDown().forEach {
                    if (it.isFile) {
                        val sticker = SPSticker()
                        sticker.packageId = packageId
                        sticker.stickerId = -1
                        sticker.stickerImg = it.absolutePath

                        stickerList.add(sticker)
                    }
                }
            }

            return stickerList
        }

        fun saveStickerJsonData(activity: Activity, sticker: SPSticker, packageId: Int, fileName: String) {

            val fileNames = fileName.split(".")

            var jsonFileName = fileName
            if (fileNames.count() > 0) {
                jsonFileName = fileNames[0]
            }

            val filePath = File(activity.filesDir, "stipop/$packageId/$jsonFileName.json")
            println("filePath : $filePath")

            val json = JSONObject()
            json.put("stickerId", sticker.stickerId)
            json.put("stickerImg", sticker.stickerImg)
            json.put("favoriteYN", sticker.favoriteYN)
            json.put("stickerId", sticker.stickerId)

            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

//            val file = File(s)
//            file.writeText(jsonString)
//
//            URL(encodedString).openStream().use { input ->
//                FileOutputStream(filePath).use { output ->
//                    input.copyTo(output)
//                }
//
//                saveStickerJsonData(sticker, packageId)
//            }

        }
    }
}