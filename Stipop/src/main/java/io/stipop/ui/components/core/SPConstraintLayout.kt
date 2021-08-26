package io.stipop.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import io.stipop.Config

class SPConstraintLayout(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
    }
}
