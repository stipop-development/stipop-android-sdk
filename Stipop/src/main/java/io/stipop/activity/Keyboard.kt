package io.stipop.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import io.stipop.R
import io.stipop.Stipop

class Keyboard(val activity: Activity) : PopupWindow() {

    private lateinit var rootView: View

    companion object {
        fun show(activity: Activity) {
            val keyboard = Keyboard(activity)
            keyboard.show()
        }
    }

    fun show() {

        if (Stipop.keyboardHeight == 0) {
            return
        }

        val inflater: LayoutInflater = this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.keyboard, null)

        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }


        // animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.BOTTOM
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.BOTTOM
            popupWindow.exitTransition = slideOut
        }

        // set size
        popupWindow.height = Stipop.keyboardHeight




        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        // Events

        view.findViewById<Button>(R.id.button_popup).setOnClickListener {
            popupWindow.dismiss()
        }

        loadPackages(view)



        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////



        // show
        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View
        popupWindow.showAtLocation(
            this.rootView,
            Gravity.BOTTOM,
            0,
            0
        )
    }

    private fun loadPackages(view: View?) {

    }
}