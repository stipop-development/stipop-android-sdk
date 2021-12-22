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
import io.stipop.delegates.PreviewDelegate
import io.stipop.models.SPSticker

internal class SpvPreview(private val activity: Activity, private val delegate: PreviewDelegate) :
    PopupWindow() {

    private val binding: ViewPreviewBinding = ViewPreviewBinding.inflate(activity.layoutInflater)
    private val rootView: View = activity.window.decorView.findViewById(android.R.id.content) as View
    private var currentSticker: SPSticker? = null
    var spvTopCoordinate : Int = 0

    init {
        contentView = binding.root
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = StipopUtils.dpToPx(170f) .toInt()

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
                toggleFavorite()
            }
            stickerImageView.setOnClickListener {
                dismiss()
                currentSticker?.let {
                    delegate.onPreviewStickerClicked(it)
                }
            }
        }
    }

    fun showOrRefresh(spSticker: SPSticker) {

        currentSticker = spSticker

        if (!isShowing) {
            showAtLocation(
                rootView,
                Gravity.TOP,
                0,
                Stipop.fromTopToVisibleFramePx - height + Config.previewPadding
            )
        }
        setSticker()
        setFavoriteState()
    }

    private fun setSticker() {
        binding.stickerImageView.loadImage(currentSticker?.stickerImgLocalFilePath ?: currentSticker?.stickerImg, false)
    }

    private fun setFavoriteState() {
        Config.getPreviewFavoriteResourceId(activity, currentSticker?.favoriteYN == "Y")
    }

    private fun toggleFavorite() {
//
//        val params = JSONObject()
//        params.put("stickerId", currentSticker.stickerId)
//
//        APIClient.put(this.activity, APIClient.APIPath.MY_STICKER_FAVORITE.rawValue + "/${Stipop.userId}", params) { response: JSONObject?, e: IOException? ->
//            // println(response)
//
//            if (null != response) {
//
//                val header = response.getJSONObject("header")
//
//                if (StipopUtils.getString(header, "status") == "success") {
//                    if (currentSticker.favoriteYN != "Y") {
//                        currentSticker.favoriteYN = "Y"
//                    } else {
//                        currentSticker.favoriteYN = "N"
//                    }
//
//                    setFavoriteState()
//
//
//                    delegate.onFavoriteChanged(currentSticker)
//
//                } else {
//                    // println("ERROR!")
//                }
//
//            } else {
//
//            }
//        }
    }


}