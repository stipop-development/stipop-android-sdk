package io.stipop.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.ui.components.core.SPImageButton

open class SPIconImageButton(context: Context, attrs: AttributeSet? = null) : SPImageButton(context, attrs) {
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        when (isEnabled) {
            true -> {
                setColorFilter(Color.parseColor(Config.themeIconTintColor))
            }
            false -> {
                setColorFilter(Color.parseColor(Config.themeIconNormalColor))
            }
        }
    }
}
