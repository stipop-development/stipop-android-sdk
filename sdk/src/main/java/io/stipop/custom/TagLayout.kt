package io.stipop.custom

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup


internal class TagLayout: ViewGroup {

    var vertical_spacing = 10
    var line_height = 0


    val itemPadding = 5
    var rowPaddingTmp = ArrayList<Int>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        assert(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED)

        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        var height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        val count = childCount

        var xpos = paddingLeft
        var ypos = paddingTop

        val childHeightMeasureSpec = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        } else {
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        }

        var rowItemCnt = 0
        var currentLineNum = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec)
                val childw = child.measuredWidth
                line_height = Math.max(line_height, child.measuredHeight + vertical_spacing)
                if (xpos + childw > width) {

                    val remains = width - (xpos + (rowItemCnt - 1) * itemPadding)
                    val hPadding = remains / 2
                    if (rowPaddingTmp.size > currentLineNum) {
                        rowPaddingTmp[currentLineNum] = hPadding
                    } else {
                        rowPaddingTmp.add(hPadding)
                    }

                    rowItemCnt = 0
                    xpos = paddingLeft
                    ypos += line_height

                    currentLineNum++
                }
                xpos += childw

                rowItemCnt++
            }
        }

        if (rowItemCnt > 0) {
            val remains = width - (xpos + (rowItemCnt - 1) * itemPadding)
            val hPadding = remains / 2
            if (rowPaddingTmp.size > currentLineNum) {
                rowPaddingTmp[currentLineNum] = hPadding
            } else {
                rowPaddingTmp.add(hPadding)
            }
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val width = r - l
        var xpos = paddingLeft
        var ypos = paddingTop

        var currentLineNum = 0

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val childw = child.measuredWidth
                val childh = child.measuredHeight
                if (xpos + childw > width) {
                    xpos = paddingLeft
                    ypos += line_height

                    currentLineNum++
                }

                var hPadding = 0
                if(rowPaddingTmp.size > currentLineNum) {
                    hPadding = rowPaddingTmp[currentLineNum]
                }

                child.layout(xpos + hPadding, ypos, xpos + childw + hPadding, ypos + childh)
                xpos += childw + itemPadding
            }
        }
    }
}