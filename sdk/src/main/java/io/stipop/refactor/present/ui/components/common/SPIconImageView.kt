package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPImageView

class SPIconImageView(context: Context, attrs: AttributeSet? = null) : SPImageView(context, attrs) {
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        when (isEnabled) {
            true -> setColorFilter(Color.parseColor(Config.themeIconTintColor))
            false -> setColorFilter(Color.parseColor(Config.themeIconNormalColor))
        }
    }
}
