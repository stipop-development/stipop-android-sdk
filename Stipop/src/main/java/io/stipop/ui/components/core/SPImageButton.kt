package io.stipop.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import io.stipop.Config

open class SPImageButton(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatImageButton(context, attrs) {

    init {
        background = null
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        setColorFilter(Color.parseColor(Config.themeIconNormalColor))
    }
}
