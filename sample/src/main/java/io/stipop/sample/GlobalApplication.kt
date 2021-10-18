package io.stipop.sample

import android.app.Application
import io.stipop.Stipop

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stipop.configure(this)
    }

}