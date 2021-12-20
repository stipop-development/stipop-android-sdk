package io.stipop.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.FragmentActivity
import io.stipop.StipopUtils

internal abstract class BaseFragmentActivity : FragmentActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        applyTheme()
    }

    abstract fun applyTheme()
}