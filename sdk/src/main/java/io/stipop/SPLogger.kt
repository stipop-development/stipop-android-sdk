package io.stipop

import android.util.Log

internal object SPLogger {

    fun log(text: String) {
        if (BuildConfig.DEBUG) {
            Log.e("Stipop Log", text)
        }
    }
}