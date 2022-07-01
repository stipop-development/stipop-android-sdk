package io.stipop.sample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.multidex.MultiDexApplication
import io.stipop.Stipop

class GlobalApplication : MultiDexApplication(),
    Application.ActivityLifecycleCallbacks
{

    private var init = false

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        Stipop.configure(this, callback = {
            Log.d(this.javaClass.name, "Use callback if you need.")
        })
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        when(init){
            true -> Stipop.configure(this, callback = {Log.d(this.javaClass.name, "Dark mode configured") })
            false -> init = true
        }
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
        hideKeyboard()
    }
    private fun hideKeyboard(){
        val keyboardPickerViewHeight = Stipop.currentKeyboardHeight
        if(keyboardPickerViewHeight != 0) {
            Stipop.hide()
        }
    }
}