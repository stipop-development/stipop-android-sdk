package io.stipop.refactor.present.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config

open class SPImageButton(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatImageButton(context, attrs) {

    init {
        background = null
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        setColorFilter(Color.parseColor(Config.themeIconNormalColor))
    }
}
