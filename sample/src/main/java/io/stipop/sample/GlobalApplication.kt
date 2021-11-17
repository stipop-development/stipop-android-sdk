package io.stipop.sample

import androidx.multidex.MultiDexApplication
import io.stipop.Stipop

class GlobalApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Stipop.configure(this)
    }

}