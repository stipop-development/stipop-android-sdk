package io.stipop.view.pickerview

import android.app.Activity
import android.widget.PopupWindow
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.StipopKeyboardHeightProvider
import io.stipop.databinding.ViewPickerBinding
import io.stipop.view.pickerview.listener.VisibleStateListener

internal class StickerPickerPopupView(
    private val activity: Activity
) : PopupWindow() {

    private var binding: ViewPickerBinding = ViewPickerBinding.inflate(activity.layoutInflater)

    var wantShowing: Boolean = false
    var lastKeyboardHeight: Int = 0

    init {
        if (Config.isPickerViewPopupWindow()) {
            Stipop.stickerPickerViewClass = StickerPickerViewClass(
                PickerViewType.ON_KEYBOARD,
                this,
                null,
                activity,
                binding
            )
            getStickerPickerKeyboardViewHeight()
        }
    }

    internal fun setDelegate(visibleDelegate: VisibleStateListener) {
        Stipop.stickerPickerViewClass?.setDelegate(visibleDelegate)
    }

    internal fun show(y: Int) {
        Stipop.stickerPickerViewClass?.show(y)
    }

    private fun getStickerPickerKeyboardViewHeight() {
        try {
            StipopKeyboardHeightProvider(activity).init().setHeightListener(object : StipopKeyboardHeightProvider.StipopKeyboardHeightListener {
                override fun onHeightChanged(fromTopToVisibleFramePx: Int, keyboardHeight: Int) {
                    val fixedKeyboardHeight = if (keyboardHeight >= 0) {
                        keyboardHeight
                    } else {
                        0
                    }
                    if (lastKeyboardHeight != fixedKeyboardHeight) {
                        Stipop.keyboardHeightDelegate?.onHeightChanged(keyboardHeight)
                        lastKeyboardHeight = fixedKeyboardHeight
                    }
                }
            })
        } catch (exception: Exception) {
            Stipop.trackError(exception)
        }
    }

}