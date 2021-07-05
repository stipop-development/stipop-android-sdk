package io.stipop.extend

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import io.stipop.Config
import io.stipop.R

class StipopImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.StipopImageView, 0, 0)
        try {
            val tintStr = ta.getString(R.styleable.StipopImageView_stipop_tint)
            val haveToSetTint = tintStr.toBoolean()
            if (haveToSetTint) {
                setTint()
            }

        } finally {
            ta.recycle()
        }

    }

    fun setTint() {
        var color = Config.themeIconTintColorDark
        if (Config.useLightMode) {
            color = Config.themeIconTintColor
        }

        setColorFilter(Color.parseColor(color))

    }
}
