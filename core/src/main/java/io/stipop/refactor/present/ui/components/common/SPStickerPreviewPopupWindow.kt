package io.stipop.refactor.present.ui.components.common

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.databinding.LayoutPreviewBinding
import io.stipop.extend.StipopImageView
import io.stipop.refactor.domain.entities.SPStickerItem

class SPStickerPreviewPopupWindow(val _targetView: View) : PopupWindow() {

    private lateinit var _binding: LayoutPreviewBinding
    private lateinit var rootView: View

    private lateinit var stickerIV: StipopImageView
    private lateinit var favoriteIV: StipopImageView

    var sticker = SPStickerItem()

    lateinit var popupWindow: PopupWindow

    fun show() {

        _binding = LayoutPreviewBinding.inflate(LayoutInflater.from(_targetView.context))
        val view = _binding.root

        popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }

        view.findViewById<ImageView>(R.id.closeIV).setOnClickListener {
            popupWindow.dismiss()
        }


        view.findViewById<ImageView>(R.id.closeIV).setImageResource(Config.getPreviewCloseResourceId(_targetView.context))

        favoriteIV = view.findViewById(R.id.favoriteIV)
        stickerIV = view.findViewById(R.id.stickerIV)

        setStickerView()

        popupWindow.showAtLocation(
            _targetView,
            Gravity.BOTTOM,
            0,
            1000 + Config.previewPadding + Utils.getNavigationBarSize(_targetView.context).y
        )
    }

    fun windowIsShowing() : Boolean {
        if (this::popupWindow.isInitialized) {
            return popupWindow.isShowing
        } else {
            return false
        }
    }

    fun windowDismiss() {
        popupWindow.dismiss()
    }

    fun setStickerView() {
        Glide.with(_targetView.context).load(sticker.stickerImg).into(stickerIV)

    }

}
