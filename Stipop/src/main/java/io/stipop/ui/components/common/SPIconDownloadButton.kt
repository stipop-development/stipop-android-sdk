package io.stipop.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.ui.components.core.SPImageButton

class SPIconDownloadButton(context: Context, attrs: AttributeSet? = null) : SPIconImageButton(context, attrs) {
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        when (isEnabled) {
            true -> {
                setImageResource(Config.getDownloadIconResourceId(context))
            }
            false -> {
                setImageResource(Config.getDownloadedIconResourceId(context))
            }
        }
    }
}
