package io.stipop.refactor.present.ui.components.common

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.stipop.Config
import io.stipop.R
import io.stipop.Stipop
import io.stipop.Utils
import io.stipop.databinding.LayoutPreviewBinding
import io.stipop.extend.StipopImageView
import io.stipop.refactor.domain.entities.SPStickerItem

class SPStickerPreviewPopupWindow(
    val _targetView: View,
    val _binding: LayoutPreviewBinding = LayoutPreviewBinding.inflate(LayoutInflater.from(_targetView.context))
) : PopupWindow(
    _binding.root, MATCH_PARENT, WRAP_CONTENT
) {
    init {
        _targetView.let {

            it.viewTreeObserver.addOnGlobalLayoutListener {
                _keyboardHeight = if (_activityHeight > it.height) {
                    _activityHeight - it.height
                } else {
                    _keyboardHeight
                }

                _isShowKeyboard = _activityHeight - it.height > 0

            }
        }
    }

    private val _metrics: DisplayMetrics
        get() {
            return Resources.getSystem().displayMetrics ?: DisplayMetrics()
        }

    private val _activityHeight: Int = _metrics.heightPixels
    private val _activityWidth: Int = _metrics.widthPixels

    private var _isShowKeyboard: Boolean = false
    private var _keyboardHeight: Int = -1
    private val _keyboardWidth: Int get() = _activityWidth

    private lateinit var stickerIV: StipopImageView
    private lateinit var favoriteIV: StipopImageView

    var sticker = SPStickerItem()

    fun show() {

        Log.e("HELLO", "SHOW")

        val view = _binding.root

        view.findViewById<ImageView>(R.id.closeIV).setOnClickListener {
            dismiss()
        }

        view.findViewById<ImageView>(R.id.closeIV)
            .setImageResource(Config.getPreviewCloseResourceId(_targetView.context))

        favoriteIV = view.findViewById(R.id.favoriteIV)
        stickerIV = view.findViewById(R.id.stickerIV)

        setStickerView()

        Log.e("TAG", "_keyboardHeight -> ${_keyboardHeight}")
        Log.e("TAG", "Config.previewPadding -> ${Config.previewPadding}")
        Log.e(
            "TAG",
            "Utils.getNavigationBarSize(_targetView.context).y -> ${Utils.getNavigationBarSize(_targetView.context).y}"
        )

        _binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        val dip = Config.previewPadding.toFloat()
        val r: Resources = _targetView.context.resources
        val px: Float = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )
        showAsDropDown(
            _targetView, 0,
            -(_binding.root.measuredHeight + Config.previewPadding + px.toInt()
//                    + Utils.getNavigationBarSize(_targetView.context).y
                    )
        )

    }

    fun setStickerView() {
        Glide.with(_targetView.context)
            .load(sticker.stickerImg)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(stickerIV)
            .clearOnDetach()
    }

}
