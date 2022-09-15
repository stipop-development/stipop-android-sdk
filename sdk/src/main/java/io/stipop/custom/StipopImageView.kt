package io.stipop.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.stipop.Config
import io.stipop.R
import io.stipop.models.SPSticker

class StipopImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
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

    fun loadImage(resUrl: String?, usePlaceHolder: Boolean = false) {
        when(usePlaceHolder){
            true -> Glide.with(context).load(resUrl).placeholder(R.color.sp_b0c7c7c7).into(this)
            false -> Glide.with(context).load(resUrl).into(this)
        }

    }

    fun loadStickerAsThumbnail(resPath: String?, resUrl: String? = null) {
        Glide.with(context)
            .load(resPath ?: "$resUrl${SPSticker.Density.STICKER_THUMB.type}")
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Handler(Looper.myLooper()!!).post {
                        Glide.with(context).load(resUrl).into(this@StipopImageView)
                    }
                    return true
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).into(this)
    }

    override fun setImageResource(resId: Int) {
        this.tag = resId
        super.setImageResource(resId)
    }
}