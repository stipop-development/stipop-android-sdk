package io.stipop.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        applyTheme()
    }

    abstract fun applyTheme()
}