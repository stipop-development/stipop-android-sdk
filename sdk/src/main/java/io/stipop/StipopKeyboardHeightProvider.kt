package io.stipop

import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.PopupWindow

object KeyboardInfo {
    const val HEIGHT_NOT_COMPUTED = -1
    const val STATE_UNKNOWN = -1
    const val STATE_CLOSED = 0
    const val STATE_OPENED = 1

    /**
     * Cached keyboard height. This will keep the last keyboard height value and not
     * it's current height
     */
    var keyboardHeight = HEIGHT_NOT_COMPUTED

    /**
     * Real time keyboard state
     */
    var keyboardState = STATE_UNKNOWN
}

internal class StipopKeyboardHeightProvider(private val activity: Activity) : PopupWindow(activity), OnGlobalLayoutListener {

    interface StipopKeyboardHeightListener {
        fun onHeightChanged(fromTopToVisibleFramePx: Int, keyboardHeight: Int)
    }

    private var resizableView: View? = null
//    private var parentView: View? = null
    private var lastKeyboardHeight = -1

    private var listener: StipopKeyboardHeightListener? = null

    init {
        try {
            contentView = View.inflate(activity, R.layout.keyboard_popup, null)
            contentView.viewTreeObserver.addOnGlobalLayoutListener(this)
            resizableView = contentView.findViewById(R.id.keyResizeContainer)
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED

            width = 0
            height = WindowManager.LayoutParams.MATCH_PARENT

        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    fun init(): StipopKeyboardHeightProvider {
        if (!isShowing) {
            val view = activity.window.decorView
            view.post { showAtLocation(view, Gravity.NO_GRAVITY, 0, 0) }
        }
        return this
    }

    fun setHeightListener(listener: StipopKeyboardHeightListener?): StipopKeyboardHeightProvider {
        this.listener = listener
        return this
    }

    private fun getScreenOrientation(): Int {
        return activity.resources.configuration.orientation
    }

    override fun onGlobalLayout() {
        onGlobalLayoutFromTopToVisibleFramePx()
    }

    private fun onGlobalLayoutFromTopToVisibleFramePx(){
        SPLogger.log("KeyboardHeight Start")
        val screenSize = Point()
        activity.windowManager.defaultDisplay.getSize(screenSize)
        val rect = Rect()
        resizableView?.getWindowVisibleDisplayFrame(rect)
        val orientation = activity.resources.configuration.orientation

        val keyboardHeight = screenSize.y + topCutoutHeight - rect.bottom
        KeyboardInfo.keyboardState = if (keyboardHeight > 0) KeyboardInfo.STATE_OPENED else KeyboardInfo.STATE_CLOSED

        if (keyboardHeight > 0) {
            KeyboardInfo.keyboardHeight = keyboardHeight
        }

        if (keyboardHeight != lastKeyboardHeight) {
            if(listener != null) {
                if(screenSize.y/2 > keyboardHeight) {
                    listener!!.onHeightChanged(rect.bottom, keyboardHeight)
                    lastKeyboardHeight = keyboardHeight
                }
            }
        }
    }

    private val topCutoutHeight: Int
        get() {
            val decorView = activity.window.decorView
            var cutOffHeight = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.rootWindowInsets?.let { windowInsets ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val displayCutout = windowInsets.displayCutout
                        if (displayCutout != null) {
                            val list = displayCutout.boundingRects
                            for (rect in list) {
                                if (rect.top == 0) {
                                    cutOffHeight += rect.bottom - rect.top
                                }
                            }
                        }
                    }
                }
            }
            return cutOffHeight
        }
}