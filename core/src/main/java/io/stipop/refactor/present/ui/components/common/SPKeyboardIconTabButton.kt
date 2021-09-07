package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config

class SPKeyboardIconTabButton(context: Context, attrs: AttributeSet? = null) :
    SPKeyboardImageTabButton(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        when (isSelected) {
            true -> {
                setColorFilter(Color.parseColor(Config.themeIconTintColor))
            }
            false -> {
                setColorFilter(Color.parseColor(Config.themeIconNormalColor))
            }
        }
    }

}
