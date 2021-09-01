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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        setTextColor(Config.getTextDownloadButtonTextColor(context))
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        val shape = GradientDrawable()
        shape.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Config.searchbarRadius.toFloat(), context.resources.displayMetrics)
        when (isEnabled) {
            true -> shape.setColor(Color.parseColor(Config.themeIconTintColor))
            false -> shape.setColor(Color.parseColor(Config.themeIconNormalColor))
        }
        background = shape
    }
}
