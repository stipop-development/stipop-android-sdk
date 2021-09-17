package io.stipop.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

abstract class BaseFragmentActivity : FragmentActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        applyTheme()
    }

    abstract fun applyTheme()
}