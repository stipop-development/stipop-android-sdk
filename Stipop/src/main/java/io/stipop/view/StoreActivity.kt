package io.stipop.view

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import io.stipop.*
import io.stipop.base.BaseFragmentActivity
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.view.adapter.StorePagerAdapter
import kotlinx.android.synthetic.main.activity_store.*

class StoreActivity : BaseFragmentActivity() {

    private lateinit var binding: ActivityStoreBinding
    private val storeAdapter: StorePagerAdapter by lazy { StorePagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeViewPager.adapter = storeAdapter

        TabLayoutMediator(storeTabLayout, storeViewPager) { tab, position ->
            when (position) {
                StorePagerAdapter.POSITION_ALL_STICKERS -> {
                    tab.text = getString(R.string.all_stickers)
                }
                StorePagerAdapter.POSITION_MY_STICKERS -> {
                    tab.text = getString(R.string.my_stickers)
                }
            }
        }.attach()

        storeViewPager.setCurrentItem(intent.getIntExtra(Constants.IntentKey.STARTING_TAB_POSITION, StorePagerAdapter.POSITION_MY_STICKERS), false)
    }

    override fun applyTheme() {
        container.setStipopBackgroundColor()
        dividingLine.setStipopUnderlineColor()
        storeTabLayout.setTabLayoutStyle()
    }
}