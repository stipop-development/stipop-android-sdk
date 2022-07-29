package io.stipop

import android.app.Activity
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

enum class StipopHeightProviderTypeEnum {
    FROM_TOP_TO_VISIBLE_FRAME_PX, KEYBOARD
}



internal class StipopHeightProvider(private val activity: Activity, private val type: StipopHeightProviderTypeEnum) : PopupWindow(activity), OnGlobalLayoutListener {

    interface StipopHeightListener {
        fun onHeightChanged(height: Int)
    }

    private val rootView: View = View(activity)
    private var listener: StipopHeightListener? = null

    private val fullSizeHeight = StipopUtils.getScreenHeight(activity)
    private var heightMax = 0

    init {
        try {
            contentView = rootView
            rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
            setBackgroundDrawable(ColorDrawable(0))
            width = 0
            height = WindowManager.LayoutParams.MATCH_PARENT
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            inputMethodMode = INPUT_METHOD_NEEDED
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    fun init(): StipopHeightProvider {
        if (!isShowing) {
            val view = activity.window.decorView
            view.post { showAtLocation(view, Gravity.NO_GRAVITY, 0, 0) }
        }
        return this
    }

    fun setHeightListener(listener: StipopHeightListener?): StipopHeightProvider {
        this.listener = listener
        return this
    }

    private fun getScreenOrientation(): Int {
        return activity.resources.configuration.orientation
    }

    override fun onGlobalLayout() {
        when(type){
            StipopHeightProviderTypeEnum.FROM_TOP_TO_VISIBLE_FRAME_PX -> onGlobalLayoutFromTopToVisibleFramePx()
            StipopHeightProviderTypeEnum.KEYBOARD -> onGlobalLayoutKeyboard()
        }

    }

    private fun onGlobalLayoutFromTopToVisibleFramePx(){
        val visibleFrameRect = Rect()
        rootView.getWindowVisibleDisplayFrame(visibleFrameRect)
        val fromTopToVisibleFramePx = visibleFrameRect.bottom

        if (listener != null) {
            if(fullSizeHeight/2 <= fromTopToVisibleFramePx) {
                listener!!.onHeightChanged(fromTopToVisibleFramePx)
            }
        }
    }

    private fun onGlobalLayoutKeyboard(){
        try {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            if (rect.bottom > heightMax) {
                heightMax = rect.bottom
            }

            var keyboardHeight = heightMax - rect.bottom
            if(Build.VERSION.SDK_INT >= 30){
                val insets: WindowInsetsCompat? = ViewCompat.getRootWindowInsets(activity.window.decorView)
                keyboardHeight -= insets?.systemWindowInsetBottom ?: 0
            }

            if (listener != null) {
                if(heightMax/2 >= keyboardHeight) {
                    listener!!.onHeightChanged(keyboardHeight)
                }
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }
}
