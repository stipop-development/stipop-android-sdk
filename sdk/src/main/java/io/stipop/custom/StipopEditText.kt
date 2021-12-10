package io.stipop.custom

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import io.stipop.Config

class StipopEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        setFont()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setFont()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
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
