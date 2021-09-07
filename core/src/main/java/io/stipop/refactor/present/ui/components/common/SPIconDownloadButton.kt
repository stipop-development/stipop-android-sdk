package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import io.stipop.Config

class SPIconDownloadButton(context: Context, attrs: AttributeSet? = null) : SPIconImageButton(context, attrs) {
    override fun draw(canvas: Canvas?) {
        when (isEnabled) {
            true -> {
                setImageResource(Config.getDownloadIconResourceId(context))
            }
            false -> {
                setImageResource(Config.getDownloadedIconResourceId(context))
            }
        }
        super.draw(canvas)
    }
}
