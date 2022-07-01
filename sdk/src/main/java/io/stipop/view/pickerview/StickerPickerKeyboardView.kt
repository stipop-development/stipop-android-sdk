package io.stipop.view.pickerview

import android.app.Activity
import android.util.Log
import android.widget.PopupWindow
import io.stipop.Config
import io.stipop.databinding.ViewPickerBinding
import io.stipop.view.pickerview.listener.VisibleStateListener

internal class StickerPickerKeyboardView(
    private val activity: Activity
) : PopupWindow() {

    private var binding: ViewPickerBinding = ViewPickerBinding.inflate(activity.layoutInflater)

    private lateinit var stickerPickerViewClass: StickerPickerViewClass

    var wantShowing: Boolean = false

    init {
        if(Config.pickerViewLayoutOnKeyboard) {
            stickerPickerViewClass = StickerPickerViewClass(
                PickerViewType.ON_KEYBOARD,
                this,
                null,
                activity,
                binding
            )
        }
    }

    internal fun setDelegate(visibleDelegate: VisibleStateListener){
        stickerPickerViewClass.setDelegate(visibleDelegate)
    }

    internal fun show(y: Int) {
        stickerPickerViewClass.show(y)
    }

    override fun dismiss() {
        stickerPickerViewClass.dismiss()
        super.dismiss()
    }

}