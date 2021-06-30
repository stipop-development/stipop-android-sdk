package io.stipop.extend

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class TagLayout: ViewGroup {

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        var curWidth: Int
        var curHeight: Int
        var curLeft: Int
        var curTop: Int
        var maxHeight: Int

        //get the available size of child view
        val childLeft = this.paddingLeft
        val childTop = this.paddingTop
        val childRight = this.measuredWidth - this.paddingRight
        val childBottom = this.measuredHeight - this.paddingBottom
        val childWidth = childRight - childLeft
        val childHeight = childBottom - childTop
        maxHeight = 0
        curLeft = childLeft
        curTop = childTop
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (child.getVisibility() === GONE) return

            //Get the maximum size of the child
            child.measure(
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
            )
            curWidth = child.getMeasuredWidth()
            curHeight = child.getMeasuredHeight()
            //wrap is reach to the end
            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft
                curTop += maxHeight
                maxHeight = 0
            }

            println("curHeight: " + curHeight)
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
            //store the max height
            if (maxHeight < curHeight) maxHeight = curHeight
            curLeft += curWidth
        }
    }

}