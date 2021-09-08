package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.stipop.Config
import io.stipop.R
import io.stipop.databinding.DialogBottomAlertBinding

class SPBottomSheetDialog(context: Context) :
    BottomSheetDialog(context) {
    private lateinit var _binding: DialogBottomAlertBinding

    private var _onClickCancelListener: OnClickCancelListener? = null
    private var _onClickConfirmListener: OnClickConfirmListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogBottomAlertBinding.inflate(layoutInflater, null, false).apply {
            (container.background as? GradientDrawable)?.setColor(Config.getAlertBackgroundColor(root.context))
            title.setTextColor(Config.getAlertTitleTextColor(root.context))
            contents.setTextColor(Config.getAlertContentsTextColor(root.context))
            cancelButton.apply {
                setTextColor(Config.getAlertButtonTextColor(root.context))
                setOnClickListener { v ->
                    _onClickCancelListener?.onClick(v)
                }
            }
            confirmButton.apply {
                setTextColor(Config.getAlertButtonTextColor(root.context))
                setOnClickListener { v ->
                    _onClickConfirmListener?.onClick(v)
                }
            }
        }
        setOnShowListener {
            window?.findViewById<View?>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)
        }
        setContentView(_binding.root)
    }

    fun setOnClickCancelListener(listener: OnClickCancelListener) {
        _onClickCancelListener = listener
    }

    fun setOnClickConfirmLListener(listener: OnClickConfirmListener) {
        _onClickConfirmListener = listener
    }
}

typealias OnClickCancelListener = View.OnClickListener
typealias OnClickConfirmListener = View.OnClickListener
