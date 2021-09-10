package io.stipop.refactor.present.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config

open class SPImageButton(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatImageButton(context, attrs) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setBackgroundColor(Color.TRANSPARENT)
    }
}
