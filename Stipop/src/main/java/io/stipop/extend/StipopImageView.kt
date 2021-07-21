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
            if (checkErrorIcon()) {
                return
            }

            val tintStr = ta.getString(R.styleable.StipopImageView_stipop_tint)
            val haveToSetTint = tintStr.toBoolean()
            if (haveToSetTint) {
                setTint()
            } else {
                setIconDefaultsColor()
            }

        } finally {
            ta.recycle()
        }

    }

    fun setIconDefaultsColor() {
        if (checkErrorIcon()) {
            return
        }

        setColorFilter(Color.parseColor(Config.themeIconColor))
    }

    fun setIconDefaultsColor40Opacity() {
        if (checkErrorIcon()) {
            return
        }

        var color = Config.themeIconColor
        color = color.replace("#", "")
        color = "#64$color"

        setColorFilter(Color.parseColor(color))

    }

    fun setTint() {
        if (checkErrorIcon()) {
            return
        }

        setColorFilter(Color.parseColor(Config.themeIconTintColor))
    }

    fun clearTint() {
        if (checkErrorIcon()) {
            return
        }

        setIconDefaultsColor()
    }

    fun checkErrorIcon(): Boolean {
        R.mipmap.error
        if (this.tag == R.mipmap.error || this.tag == R.mipmap.error_dark) {
            return true
        }
        return false
    }

    override fun setImageResource(resId: Int) {
        this.tag = resId
        super.setImageResource(resId)
    }
}
