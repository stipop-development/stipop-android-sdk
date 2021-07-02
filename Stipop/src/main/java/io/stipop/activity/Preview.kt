package io.stipop.activity

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import io.stipop.*
import io.stipop.model.SPSticker
import org.json.JSONObject
import java.io.IOException

class Preview(val activity: Activity, val keyboard: Keyboard) : PopupWindow() {

    private lateinit var rootView: View

    private lateinit var stickerIV: ImageView
    private lateinit var favoriteIV: ImageView

    var sticker = SPSticker()

    lateinit var popupWindow: PopupWindow

    fun show() {

        if (Stipop.keyboardHeight == 0) {
            return
        }

        val view = View.inflate(this.activity, R.layout.activity_preview,null)

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


        favoriteIV = view.findViewById(R.id.favoriteIV)
        stickerIV = view.findViewById(R.id.stickerIV)

        favoriteIV.setOnClickListener {
            setFavorite()
        }

        setStickerView()

        // show
        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View
        popupWindow.showAtLocation(
            this.rootView,
            Gravity.BOTTOM,
            0,
            Stipop.keyboardHeight + Utils.getNavigationBarSize(this.activity).y
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
        Glide.with(this.activity).load(sticker.stickerImg).into(stickerIV)

        if (sticker.favoriteYN == "Y") {
            favoriteIV.setImageResource(R.mipmap.ic_favorites_on)
        } else {
            favoriteIV.setImageResource(R.mipmap.ic_favorites_off)
        }
    }

    fun setFavorite() {

        val params = JSONObject()
        params.put("stickerId", sticker.stickerId)

        APIClient.put(this.activity, APIClient.APIPath.MY_STICKER_FAVORITE.rawValue + "/${Stipop.userId}", params) { response: JSONObject?, e: IOException? ->
            println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (Utils.getString(header, "status") == "success") {
                    if (sticker.favoriteYN != "Y") {
                        favoriteIV.setImageResource(R.mipmap.ic_favorites_on)
                        sticker.favoriteYN = "Y"
                    } else {
                        favoriteIV.setImageResource(R.mipmap.ic_favorites_off)
                        sticker.favoriteYN = "N"
                    }

                    keyboard.changeFavorite(sticker.stickerId, sticker.favoriteYN, sticker.packageId)

                } else {
                    println("ERROR!")
                }

            } else {

            }
        }

    }

}