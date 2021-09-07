package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPTextView

class SPPackageNameLabel(context: Context, attrs: AttributeSet? = null) : SPTextView(context, attrs) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        setTextColor(Config.getPackageNameTextColor(context))
    }
}
