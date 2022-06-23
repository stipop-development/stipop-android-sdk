package io.stipop.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onResume() {
        super.onResume()
        applyTheme()
    }

    abstract fun applyTheme()
}