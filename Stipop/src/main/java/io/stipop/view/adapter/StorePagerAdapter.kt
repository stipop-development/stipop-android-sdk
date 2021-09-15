package io.stipop.view.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.stipop.view.AllStickerFragment
import io.stipop.view.MyStickerFragment

class StorePagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = SparseArray<Fragment>()

    init {
        fragments.apply {
            put(
                POSITION_ALL_STICKERS,
                AllStickerFragment.newInstance()
            )
            put(
                POSITION_MY_STICKERS,
                MyStickerFragment.newInstance()
            )
        }
    }

    override fun getItemCount(): Int = fragments.size()

    override fun createFragment(position: Int): Fragment = fragments.get(position)

    companion object {
        const val POSITION_ALL_STICKERS = 0
        const val POSITION_MY_STICKERS = 1
    }
}