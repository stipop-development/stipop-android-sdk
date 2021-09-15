package io.stipop.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import io.stipop.R
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.view.adapter.StorePagerAdapter
import kotlinx.android.synthetic.main.activity_store.*

class StoreActivity : FragmentActivity() {

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
        }

//
//        this.context = this
//
//        tab = intent.getIntExtra("tab", 1)
//
//        val drawable = containerLL.background as GradientDrawable
//        drawable.setColor(Color.parseColor(Config.themeBackgroundColor)) // solid  color
//
//        navigationBarLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
//
//        underLineV.setBackgroundColor(Config.getUnderLineColor(context))
//
//
//        allV.setBackgroundColor(Config.getStoreNavigationTextColor(context, true))
//        myV.setBackgroundColor(Config.getStoreNavigationTextColor(context, true))
//
//        val fm: FragmentManager = supportFragmentManager
//
//        if (tab == 2) {
//            fragmentTransaction = fm.beginTransaction()
//            fragmentTransaction.add(R.id.fragmentFL, MyStickerFragment())
//            fragmentTransaction.commit()
//        } else {
//            fragmentTransaction = fm.beginTransaction()
//            fragmentTransaction.add(R.id.fragmentFL, AllStickerFragment())
//            fragmentTransaction.commit()
//        }
//
//        allTabLL.setOnClickListener {
//            changeTabs(1)
//
//            fragmentTransaction = fm.beginTransaction()
//            fragmentTransaction.replace(R.id.fragmentFL, AllStickerFragment())
//            fragmentTransaction.commit()
//        }
//
//        myTabLL.setOnClickListener {
//            changeTabs(2)
//
//            fragmentTransaction = fm.beginTransaction()
//            fragmentTransaction.replace(R.id.fragmentFL, MyStickerFragment())
//            fragmentTransaction.commit()
//        }
//
//        changeTabs(tab)

    }

//    private fun changeTabs(type: Int) {
//        allTV.setTextColor(Config.getStoreNavigationTextColor(context, false))
//        myTV.setTextColor(Config.getStoreNavigationTextColor(context, false))
//
//        allV.visibility = View.INVISIBLE
//        myV.visibility = View.INVISIBLE
//
//        if (type == 1) {
//            allTV.setTextColor(Config.getStoreNavigationTextColor(context, true))
//            allV.visibility = View.VISIBLE
//        } else {
//            myTV.setTextColor(Config.getStoreNavigationTextColor(context, true))
//            myV.visibility = View.VISIBLE
//        }
//    }

}