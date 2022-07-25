package io.stipop.view

import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import io.stipop.*
import io.stipop.adapter.StorePagerAdapter
import io.stipop.api.StipopApi
import io.stipop.base.BaseFragmentActivity
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.body.UserIdBody
import io.stipop.models.enum.StipopApiEnum
import kotlinx.coroutines.*
import retrofit2.HttpException

internal class StoreActivity : BaseFragmentActivity() {

    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var binding: ActivityStoreBinding
    private val storeAdapter: StorePagerAdapter by lazy { StorePagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainScope.launch {
        with(binding) {
            storeViewPager.adapter = storeAdapter
            TabLayoutMediator(storeTabLayout, storeViewPager) { tab, position ->
                when (position) {
                    StorePagerAdapter.POSITION_ALL_STICKERS -> {
                        tab.text = getString(R.string.all_stickers)
                    }
                    StorePagerAdapter.POSITION_NEW_STICKERS -> {
                        tab.text = getString(R.string.news_tab)
                    }
                    StorePagerAdapter.POSITION_MY_STICKERS -> {
                        tab.text = getString(R.string.my_stickers)
                    }
                }
            }.attach()

                    storeViewPager.apply {
                        registerOnPageChangeCallback(callBack)
                        setCurrentItem(
                            intent.getIntExtra(
                                Constants.IntentKey.STARTING_TAB_POSITION,
                                StorePagerAdapter.POSITION_MY_STICKERS
                            ), false
                        )
                    }
                }
        }
        PackageDownloadEvent.liveData.observe(this) {
            Toast.makeText(this, getString(R.string.download_done), Toast.LENGTH_SHORT).show()
        }
    }

    override fun applyTheme() {
        with(binding) {
            container.setStipopBackgroundColor()
            dividingLine.setStipopUnderlineColor()
            storeTabLayout.setTabLayoutStyle()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        PackageDownloadEvent.onDestroy()
    }

    private val callBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
                when (position) {
                    StorePagerAdapter.POSITION_ALL_STICKERS -> {
                        trackViewStore()
                    }
                    StorePagerAdapter.POSITION_NEW_STICKERS -> {
                        trackViewNew()
                    }
                    StorePagerAdapter.POSITION_MY_STICKERS -> {
                        trackViewSticker()
                    }
                }
            }
        }

    internal fun trackViewStore(){
        scope.launch {
            val apiService = StipopApi.create()
            val response = apiService.trackViewStore(UserIdBody(Stipop.userId))
            if (response.code() == 401) {
                Stipop.sAuthDelegate?.httpException(StipopApiEnum.TRACK_VIEW_STORE, HttpException(response))
            }
        }
    }

    internal fun trackViewNew(){
        scope.launch {
            val apiService = StipopApi.create()
            val response = apiService.trackViewNew(UserIdBody(Stipop.userId))
            if(response.code() == 401){
                Stipop.sAuthDelegate?.httpException(StipopApiEnum.TRACK_VIEW_NEW, HttpException(response))
            }
        }
    }

    internal fun trackViewSticker(){
        scope.launch {
            val apiService = StipopApi.create()
            val response = apiService.trackViewMySticker(UserIdBody(Stipop.userId))
            if(response.code() == 401){
                Stipop.sAuthDelegate?.httpException(StipopApiEnum.TRACK_VIEW_MY_STICKER, HttpException(response))
            }
        }
    }
}