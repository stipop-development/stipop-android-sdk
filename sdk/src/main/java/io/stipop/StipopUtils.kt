package io.stipop

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.Display
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import io.stipop.Config.Companion.getErrorImage
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


internal object StipopUtils {

    fun pxToDp(px: Long): Float {
        return px * Resources.getSystem().displayMetrics.density
    }

    // 키보드 보이기
    fun showKeyboard(context: Context) {
        val immhide = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        immhide.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

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
                            sticker.stickerId = getInt(json, "stickerId")
                            sticker.stickerImg = getString(json, "stickerImg")
                            sticker.favoriteYN = getString(json, "favoriteYN")
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

    fun getNavigationBarSize(context: Context): Point {
        val appUsableSize = getAppUsableScreenSize(context)
        val realScreenSize = getRealScreenSize(context)

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
        }

        // navigation bar at the bottom
        return if (appUsableSize.y < realScreenSize.y) {
            Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
        } else Point()

        // navigation bar is not present
    }

    private fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size)
        } else {
            try {
                size.x =
                    (Display::class.java.getMethod("getRawWidth").invoke(display) as Int)
                size.y =
                    (Display::class.java.getMethod("getRawHeight").invoke(display) as Int)
            } catch (ignored: IllegalAccessException) {
            } catch (ignored: InvocationTargetException) {
            } catch (ignored: NoSuchMethodException) {
            }
        }
        return size
    }

    private fun getAppUsableScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    fun getScreenHeight(context: Context): Int {
        var height = 0
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        height = size.y
        return height
    }

    fun getScreenWidth(context: Context): Int {
        var width = 0
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        return width
    }

    fun alert(context: Context?, msg: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(msg).setCancelable(false).setPositiveButton(
            context?.getString(R.string.okay)
        ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }


    fun getFloat(json: JSONObject?, key: String?): Float {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getDouble(key).toFloat()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return 0.0f
    }

    fun getDouble(json: JSONObject?, key: String?, defaultValue: Double): Double {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getDouble(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return defaultValue
    }


    fun getString(json: JSONObject?, key: String?, defaultValue: String): String {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getString(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return defaultValue
    }

    fun getInt(json: JSONObject?, key: String?): Int {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getInt(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return 0
    }

    fun getInt(json: JSONObject?, key: String?, defaultValue: Int): Int {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getInt(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return defaultValue
    }

    fun getBoolean(json: JSONObject?, key: String?): Boolean {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getBoolean(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun getBoolean(json: JSONObject?, key: String?, defaultValue: Boolean): Boolean {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getBoolean(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return defaultValue
    }

    fun getResource(imageName: String?, context: Context): Int {
        var id = context.resources.getIdentifier(imageName, "mipmap", context.packageName)
        if (id < 1) {
            id = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            if (id < 1) {
                id = getErrorImage()
            }
        }
        return id
    }

    fun getString(json: JSONObject?, key: String?): String {
        if (json != null && json.has(key) && !json.isNull(key)) {
            try {
                return json.getString(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    fun dpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}