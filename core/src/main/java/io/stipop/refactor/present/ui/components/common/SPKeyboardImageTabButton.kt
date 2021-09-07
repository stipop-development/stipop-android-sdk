package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPImageButton

open class SPKeyboardImageTabButton(context: Context, attrs: AttributeSet? = null) : SPImageButton(context, attrs) {

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        when (isSelected) {
            true -> {
                setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            }
            false -> {
                setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            }
        }
    }
}
