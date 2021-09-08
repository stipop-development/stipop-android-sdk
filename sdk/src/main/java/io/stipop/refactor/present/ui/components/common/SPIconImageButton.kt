package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPImageButton

open class SPIconImageButton(context: Context, attrs: AttributeSet? = null) : SPImageButton(context, attrs) {
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
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
