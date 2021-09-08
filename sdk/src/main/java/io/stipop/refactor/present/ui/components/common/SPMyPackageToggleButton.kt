package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPSwitch

class SPMyPackageToggleButton(context: Context, attrs: AttributeSet? = null) : SPSwitch(context, attrs) {

    init {
        showText = true
        thumbDrawable = null
        trackDrawable = null
        textOn = Config.getMyPackageActiveText(context)
        textOff = Config.getMyPackageHiddenText(context)
    }

    override fun draw(canvas: Canvas?) {
        setTextColor(Config.getActiveHiddenStickerTextColor(context))
        val shape = GradientDrawable()
        shape.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Config.searchbarRadius.toFloat(), context.resources.displayMetrics)
        when (isChecked) {
            true -> {
//                setBackgroundColor(Config.getActiveStickerBackgroundColor(context))
                shape.setColor(Config.getActiveStickerBackgroundColor(context))
            }
            false -> {
//                setBackgroundColor(Config.getHiddenStickerBackgroundColor(context))
                shape.setColor(Config.getHiddenStickerBackgroundColor(context))
            }
        }
//        shape.setColor(Config.getSearchBarBackgroundColor(context))
        background = shape
        super.draw(canvas)
    }
}
