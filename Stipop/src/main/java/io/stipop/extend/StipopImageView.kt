package io.stipop.extend

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import io.stipop.Config
import io.stipop.R
import java.lang.Exception

class StipopImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setTint() {
        var color = Config.themeIconTintColorDark
        if (Config.useLightMode) {
            color = Config.themeIconTintColor
        }

        setColorFilter(Color.parseColor(color))

    }
}
