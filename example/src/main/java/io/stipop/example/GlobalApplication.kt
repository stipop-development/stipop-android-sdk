package io.stipop.example

import android.app.Application
import android.content.Context
import io.stipop.Stipop

class GlobalApplication : Application() {
    companion object {
        lateinit var instance: GlobalApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stipop.configure(this)
    }

    fun context(): Context = applicationContext
}