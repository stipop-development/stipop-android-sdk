package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPButton

class SPTextButton(context: Context, attrs: AttributeSet? = null): SPButton(context, attrs) {

    init {
        background = null
    }
}
