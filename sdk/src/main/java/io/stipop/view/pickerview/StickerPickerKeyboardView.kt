package io.stipop.view.pickerview

import android.app.Activity
import android.widget.PopupWindow
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.databinding.ViewPickerBinding
import io.stipop.view.pickerview.listener.VisibleStateListener

internal class StickerPickerKeyboardView(
    private val activity: Activity
) : PopupWindow() {

    private var binding: ViewPickerBinding = ViewPickerBinding.inflate(activity.layoutInflater)

    var wantShowing: Boolean = false

    init {
        if(Config.pickerViewLayoutOnKeyboard) {
            Stipop.stickerPickerViewClass = StickerPickerViewClass(
                PickerViewType.ON_KEYBOARD,
                this,
                null,
                activity,
                binding
            )
        }
    }

    internal fun setDelegate(visibleDelegate: VisibleStateListener){
        Stipop.stickerPickerViewClass?.setDelegate(visibleDelegate)
    }

    internal fun show(y: Int) {
        Stipop.stickerPickerViewClass?.show(y)
    }

    override fun dismiss() {
        Stipop.stickerPickerViewClass?.dismiss()
        super.dismiss()
    }

}