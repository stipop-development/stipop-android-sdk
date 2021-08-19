package io.stipop.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import io.stipop.Config
import io.stipop.R
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.fragment.AllStickerFragment
import io.stipop.fragment.MyStickerFragment

class StoreActivity : FragmentActivity() {

    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var binding: ActivityStoreBinding

    private var tab = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tab = intent.getIntExtra("tab", 1)

        binding.navigationBarLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))

        binding.underLineV.setBackgroundColor(Config.getUnderLineColor(this))


        binding.allV.setBackgroundColor(Config.getStoreNavigationTextColor(this, true))
        binding.myV.setBackgroundColor(Config.getStoreNavigationTextColor(this, true))

        binding.allTabLL.setOnClickListener {
            changeTabs(1)
            Log.d(this::class.simpleName, "ALL")
        }

        binding.myTabLL.setOnClickListener {
            changeTabs(2)
            Log.d(this::class.simpleName, "MY")
        }

        changeTabs(tab)
    }

    override fun onResume() {
        super.onResume()

        binding.containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
    }

    private fun changeTabs(type: Int) {

        Log.d(this::class.simpleName, "changeTabs: type -> $type")

        if (type == 1) {
            fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentFL, AllStickerFragment())
            fragmentTransaction.commit()

            binding.allTV.setTextColor(Config.getStoreNavigationTextColor(this, true))
            binding.allV.visibility = View.VISIBLE

            binding.myTV.setTextColor(Config.getStoreNavigationTextColor(this, false))
            binding.myV.visibility = View.INVISIBLE


        } else if (type == 2) {
            fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentFL, MyStickerFragment())
            fragmentTransaction.commit()

            binding.allTV.setTextColor(Config.getStoreNavigationTextColor(this, false))
            binding.allV.visibility = View.INVISIBLE

            binding.myTV.setTextColor(Config.getStoreNavigationTextColor(this, true))
            binding.myV.visibility = View.VISIBLE
        }
    }

}