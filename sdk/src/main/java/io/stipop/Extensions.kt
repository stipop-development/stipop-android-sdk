package io.stipop

import android.R
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.annotation.AnyThread
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

fun View.setStipopBackgroundColor() {
    setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
}

fun View.setStipopUnderlineColor() {
    setBackgroundColor(
        ContextCompat.getColor(
            context,
            if (Config.themeUseLightMode) io.stipop.R.color.c_f7f8f9 else io.stipop.R.color.c_2e363a
        )
    )
}

fun TabLayout.setTabLayoutStyle() {
    setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
    ColorStateList(
        arrayOf(
            intArrayOf(R.attr.state_selected),
            intArrayOf(-R.attr.state_selected),
        ),
        intArrayOf(
            if (Config.themeUseLightMode) ContextCompat.getColor(
                context,
                io.stipop.R.color.c_374553
            ) else ContextCompat.getColor(context, io.stipop.R.color.c_f3f4f5),
            if (Config.themeUseLightMode) ContextCompat.getColor(
                context,
                io.stipop.R.color.c_c6c8cf
            ) else ContextCompat.getColor(context, io.stipop.R.color.c_646f7c)
        )
    ).let {
        tabTextColors = it
    }
    setSelectedTabIndicatorColor(
        if (Config.themeUseLightMode) ContextCompat.getColor(
            context,
            io.stipop.R.color.c_292929
        ) else ContextCompat.getColor(context, io.stipop.R.color.c_f7f8f9)
    )
}

@AnyThread
suspend fun delayedTextFlow(request: String): String =
    withContext(Dispatchers.Default) {
        delay(600)
        request
    }

fun <T> Collection<T>?.isNullOrNotEnough(): Boolean {
    if (this == null) {
        return true
    }
    return this.size < 5
}