package io.stipop.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.ui.components.core.SPImageButton
import io.stipop.ui.components.core.SPTextView

class SPArtistNameLabel(context: Context, attrs: AttributeSet? = null) : SPTextView(context, attrs) {

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        setTextColor(Config.getArtistNameTextColor(context))
    }
}
