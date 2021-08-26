package io.stipop.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import com.github.mmin18.widget.RealtimeBlurView
import io.stipop.Config
import io.stipop.ui.utils.ColorTransparentUtils

/**
 * source come from https://github.com/mmin18/RealtimeBlurView/blob/master/library/src/com/github/mmin18/widget/RealtimeBlurView.java
 */
class SPBlurView(context: Context, attrs: AttributeSet? = null) : RealtimeBlurView(context, attrs) {
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val a = ColorTransparentUtils.transparentColor(Color.parseColor(Config.themeBackgroundColor), 60)
        setOverlayColor(Color.parseColor(a))
    }
}
