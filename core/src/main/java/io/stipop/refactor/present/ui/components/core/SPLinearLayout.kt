package io.stipop.refactor.present.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import io.stipop.Config

open class SPLinearLayout(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
    }
}
