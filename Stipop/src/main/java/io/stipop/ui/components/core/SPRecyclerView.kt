package io.stipop.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config

class SPRecyclerView(context: Context, attrs: AttributeSet? = null) :
    RecyclerView(context, attrs) {
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
    }
}
