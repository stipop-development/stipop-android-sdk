package io.stipop.custom

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import io.stipop.Config
import io.stipop.R

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

        setHintTextColor(ContextCompat.getColor(context, R.color.edittext_hint))

        setTextCursorDrawable(R.drawable.edittext_cursor)

        if (Config.FontStyle.fontFace != null) {
            this.typeface = Config.FontStyle.fontFace
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.letterSpacing = Config.FontStyle.fontCharacter
        }
    }
}
