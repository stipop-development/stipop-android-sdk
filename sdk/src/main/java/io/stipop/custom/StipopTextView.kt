package io.stipop.custom

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import io.stipop.Config

class StipopTextView : AppCompatTextView {
    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFont()
    }

    private fun setFont() {
        if (Config.FontStyle.fontFace != null) {
            this.typeface = Config.FontStyle.fontFace
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.letterSpacing = Config.FontStyle.fontCharacter
        }
    }
}
