package io.stipop.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.Config
import io.stipop.R

class StipopImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    enum class Density(val density: String?) {
        DEFAULT(null), SMALL("100x100")
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
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

    fun loadImage(resUrl: String?, usePlaceHolder: Boolean = true) {
        Glide.with(context)
            .load(resUrl)
            .placeholder(if (usePlaceHolder) R.color.b0_c7c7c7 else R.color.transparent).into(this)
    }

    override fun setImageResource(resId: Int) {
        this.tag = resId
        super.setImageResource(resId)
    }
}