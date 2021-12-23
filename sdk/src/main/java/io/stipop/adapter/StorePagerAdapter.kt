package io.stipop.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.stipop.view.StoreHomeFragment
import io.stipop.view.StoreMyStickerFragment
import io.stipop.view.StoreNewStickerFragment

internal class StorePagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = SparseArray<Fragment>()

    init {
        fragments.apply {
            put(
                POSITION_ALL_STICKERS,
                StoreHomeFragment.newInstance()
            )
            put(
                POSITION_NEW_STICKERS,
                StoreNewStickerFragment.newInstance()
            )
            put(
                POSITION_MY_STICKERS,
                StoreMyStickerFragment.newInstance()
            )
        }
    }

    override fun getItemCount(): Int = fragments.size()

    override fun createFragment(position: Int): Fragment = fragments.get(position)

    companion object {
        const val POSITION_ALL_STICKERS = 0
        const val POSITION_NEW_STICKERS = 1
        const val POSITION_MY_STICKERS = 2
    }
}