package io.stipop.sample

import android.util.Log
import androidx.multidex.MultiDexApplication
import io.stipop.Stipop

class GlobalApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Stipop.configure(this, callback = {
            Log.d(this.javaClass.name, "Use callback if you need.")
        })
    }

}