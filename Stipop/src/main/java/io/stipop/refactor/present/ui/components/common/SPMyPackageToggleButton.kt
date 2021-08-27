package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
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

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        setTextColor(Config.getActiveHiddenStickerTextColor(context))
        when (isChecked) {
            true -> {
                setBackgroundColor(Config.getActiveStickerBackgroundColor(context))
            }
            false -> {
                setBackgroundColor(Config.getHiddenStickerBackgroundColor(context))
            }
        }
    }
}
