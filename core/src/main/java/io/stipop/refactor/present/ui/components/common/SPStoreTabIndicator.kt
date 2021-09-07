package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPView

class SPStoreTabIndicator(context: Context, attrs: AttributeSet? = null) : SPView(context, attrs) {
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        setBackgroundColor(Config.getStoreTabIndicatorColor(context, isSelected))
    }
}
