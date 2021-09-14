package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config

class SPIconDownloadButton(context: Context, attrs: AttributeSet? = null) : SPIconImageButton(context, attrs) {
    override fun draw(canvas: Canvas?) {
        when (isEnabled) {
            true -> {
                setColorFilter(Color.parseColor(Config.themeIconTintColor))
                setImageResource(Config.getDownloadIconResourceId(context))
            }
            false -> {
                setColorFilter(Color.parseColor(Config.themeIconNormalColor))
                setImageResource(Config.getDownloadedIconResourceId(context))
            }
        }
        super.draw(canvas)
    }
}
