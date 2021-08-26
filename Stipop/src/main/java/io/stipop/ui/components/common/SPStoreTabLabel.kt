package io.stipop.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.ui.components.core.SPTextView

class SPStoreTabLabel(context: Context, attrs: AttributeSet? = null) : SPTextView(context, attrs) {
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        setTextColor(Config.getStoreTabLabelColor(context, isSelected))
    }
}
