package io.stipop.api

import android.app.Activity
import io.stipop.Config
import io.stipop.StipopUtils
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread


internal class APIClient {

    enum class APIPath(val rawValue: String) {
        MY_STICKER_FAVORITE("/mysticker/favorite"),
    }

    companion object {
        fun put(
            activity: Activity,
            path: String,
            parameters: JSONObject?,
            responseCallback: (response: JSONObject?, e: IOException?) -> Unit
        ) {
            thread(start = true) {

                val resolvedPath = Config.baseUrl + path + "?platform=android-sdk"

                val url = URL(resolvedPath)

                val huc = url.openConnection() as HttpURLConnection
                huc.requestMethod = "PUT"
                huc.doOutput = true
                huc.doInput = true

                huc.setRequestProperty("apikey", Config.stipopConfigData.apiKey)
                huc.setRequestProperty("Content-Type", "application/json;charset=utf-8")
                huc.setRequestProperty("Accept", "application/json")


                val writer = OutputStreamWriter(huc.outputStream)
                writer.write(parameters.toString())
                writer.flush()
                writer.close()

                val buffered = if (huc.responseCode in 100..399) {
                    BufferedReader(InputStreamReader(huc.inputStream))
                } else {
                    BufferedReader(InputStreamReader(huc.errorStream))
                }

                val content = StringBuilder()
                while (true) {
                    val data = buffered.readLine() ?: break
                    content.append(data)
                }

                buffered.close()
                huc.disconnect()

                activity.runOnUiThread {
                    val response = JSONObject(content.toString())
                    responseCallback(response, null)
                }
            }
        }

    }
}