package io.stipop.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import io.stipop.Config

class SPLinearLayout(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        canvas?.drawColor(Color.parseColor(Config.themeBackgroundColor))
    }
}
