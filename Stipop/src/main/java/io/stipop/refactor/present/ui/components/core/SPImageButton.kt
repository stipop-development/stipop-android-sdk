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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        setColorFilter(Color.parseColor(Config.themeIconNormalColor))
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        setColorFilter(Color.parseColor(Config.themeIconNormalColor))
    }
}
