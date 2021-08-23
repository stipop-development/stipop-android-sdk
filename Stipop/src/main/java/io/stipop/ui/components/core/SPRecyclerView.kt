package io.stipop.ui.components.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config

class SPRecyclerView(context: Context, attrs: AttributeSet?) :
    RecyclerView(context, attrs) {

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)

        c?.drawColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
    }
}
