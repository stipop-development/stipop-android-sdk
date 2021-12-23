package io.stipop.view

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.StipopUtils
import io.stipop.databinding.ViewPreviewBinding
import io.stipop.event.PreviewDelegate
import io.stipop.models.SPSticker
import io.stipop.view.viewmodel.SpvModel

internal class SpvPreview(
    private val activity: Activity,
    private val delegate: PreviewDelegate,
    private val spvModel: SpvModel
) :
    PopupWindow() {

    private val binding: ViewPreviewBinding = ViewPreviewBinding.inflate(activity.layoutInflater)
    private val rootView: View =
        activity.window.decorView.findViewById(android.R.id.content) as View
    private var currentSticker: SPSticker? = null
    var spvTopCoordinate: Int = 0

    init {
        contentView = binding.root
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = StipopUtils.dpToPx(170f).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setIsClippedToScreen(true)
        } else {
            isClippingEnabled = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 10.0F
        }

        with(binding) {
            closeImageView.setImageResource(Config.getPreviewCloseResourceId(activity))
            closeImageView.setOnClickListener {
                dismiss()
            }
            favoriteImageView.setOnClickListener {
                updateFavorite()
            }
            stickerImageView.setOnClickListener {
                currentSticker?.let {
                    delegate.onPreviewStickerClicked(it)
                }
                dismiss()
            }
        }
    }

    fun showOrUpdate(spSticker: SPSticker): Boolean {
        val isSame = currentSticker == spSticker
        currentSticker = spSticker
        if (!isShowing) {
            showAtLocation(
                rootView,
                Gravity.TOP,
                0,
                Stipop.fromTopToVisibleFramePx - height + Config.previewPadding
            )
        }
        setStickerUi()
        setFavoriteUi()
        return isShowing && isSame
    }

    private fun setStickerUi() {
        binding.stickerImageView.loadImage(
            currentSticker?.stickerImgLocalFilePath ?: currentSticker?.stickerImg, false
        )
    }

    private fun setFavoriteUi() {
        binding.favoriteImageView.setImageResource(
            Config.getPreviewFavoriteResourceId(
                activity,
                currentSticker?.favoriteYN == "Y"
            )
        )
    }

    private fun updateFavorite() {
        currentSticker?.let {
            spvModel.putFavorites(it, onSuccess = { spSticker ->
                activity.runOnUiThread {
                    setFavoriteUi()
                }
                delegate.onPreviewFavoriteChanged(spSticker)
            })
        }
    }

    override fun dismiss() {
        super.dismiss()
        currentSticker = null
    }
}