package io.stipop.api

import android.app.Activity
import io.stipop.Config
import io.stipop.Utils
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread


internal class APIClient {

    enum class APIPath(val rawValue: String) {
        SEARCH("/search"),
        SEARCH_KEYWORD("/search/keyword"),
        MY_STICKER_FAVORITE("/mysticker/favorite"),
    }

    companion object {

        fun get(
            activity: Activity,
            path: String,
            responseCallback: (response: JSONObject?, e: IOException?) -> Unit
        ) {
            get(activity, path, null, responseCallback)
        }

        fun get(
            activity: Activity,
            path: String,
            parameters: JSONObject?,
            responseCallback: (response: JSONObject?, e: IOException?) -> Unit
        ) {

            thread(start = true) {
                // parameters
                var resolvedPath = Config.baseUrl + path

                if (parameters != null && parameters.keys().hasNext()) {
                    resolvedPath += "?"
                    resolvedPath += getQuery(parameters)
                    resolvedPath += "&platform=android-sdk"
                } else {
                    resolvedPath += "?platform=android-sdk"
                }

                // println(resolvedPath)

                val url = URL(resolvedPath)

                val huc = url.openConnection() as HttpURLConnection
                huc.requestMethod = "GET"
                huc.setRequestProperty("apikey", Config.apikey)


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

        private fun getQuery(params: JSONObject): String {
            val result = java.lang.StringBuilder()
            var first = true

            params.keys()

            for (key in params.keys()) {
                if (first) {
                    first = false
                } else {
                    result.append("&")
                }

                val value = Utils.getString(params, key)
                result.append(URLEncoder.encode(key, "UTF-8"))
                result.append("=")
                result.append(URLEncoder.encode(value, "UTF-8"))
            }
            return result.toString()
        }

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

                huc.setRequestProperty("apikey", Config.apikey)
                huc.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                huc.setRequestProperty("Accept", "application/json");


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