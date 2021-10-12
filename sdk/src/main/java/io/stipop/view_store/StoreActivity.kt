package io.stipop.view_store

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import io.stipop.*
import io.stipop.base.BaseFragmentActivity
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.adapter.StorePagerAdapter
import io.stipop.api.StipopApi
import io.stipop.models.body.UserIdBody
import kotlinx.android.synthetic.main.activity_store.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StoreActivity : BaseFragmentActivity() {

    val scope = CoroutineScope(Job() + Dispatchers.IO)
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

        storeViewPager.registerOnPageChangeCallback(callBack)
        storeViewPager.setCurrentItem(
            intent.getIntExtra(
                Constants.IntentKey.STARTING_TAB_POSITION,
                StorePagerAdapter.POSITION_MY_STICKERS
            ), false
        )
    }

    override fun applyTheme() {
        container.setStipopBackgroundColor()
        dividingLine.setStipopUnderlineColor()
        storeTabLayout.setTabLayoutStyle()
    }

    private val callBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val userId = Stipop.userId
            scope.launch {
                when (position) {
                    StorePagerAdapter.POSITION_ALL_STICKERS -> {
                        StipopApi.create().trackViewStore(UserIdBody(userId))
                    }
                    StorePagerAdapter.POSITION_MY_STICKERS -> {
                        StipopApi.create().trackViewMySticker(UserIdBody(userId))
                    }
                }
            }
        }
    }
}