package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPButton

class SPTextDownloadButton(context: Context, attrs: AttributeSet? = null): SPButton(context, attrs) {

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        setTextColor(Config.getTextDownloadButtonTextColor(context))

        val shape = GradientDrawable()
        shape.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Config.searchbarRadius.toFloat(), context.resources.displayMetrics)
        when (isEnabled) {
            true -> shape.setColor(Color.parseColor(Config.themeIconTintColor))
            false -> shape.setColor(Color.parseColor(Config.themeIconNormalColor))
        }
        background = shape
    }
}
