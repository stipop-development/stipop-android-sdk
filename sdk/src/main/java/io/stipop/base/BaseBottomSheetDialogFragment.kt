package io.stipop.base

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onResume() {
        super.onResume()
        applyTheme()
    }

    abstract fun applyTheme()
}