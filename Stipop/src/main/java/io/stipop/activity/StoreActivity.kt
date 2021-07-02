package io.stipop.activity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import io.stipop.APIClient
import io.stipop.Config
import io.stipop.R
import io.stipop.Stipop
import io.stipop.fragment.AllStickerFragment
import io.stipop.fragment.MyStickerFragment
import kotlinx.android.synthetic.main.activity_store.*
import org.json.JSONObject
import java.io.IOException

class StoreActivity: FragmentActivity() {

    lateinit var context: Context

    lateinit var fragmentTransaction: FragmentTransaction

    var tab = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        this.context = this

        tab = intent.getIntExtra("tab", 1)

        val drawable = containerLL.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.themeBackgroundColor)) // solid  color

        navigationBarLL.setBackgroundColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))

        underLineV.setBackgroundColor(Config.getUnderLineColor(context))


        allV.setBackgroundColor(Config.getStoreNavigationTextColor(context, true))
        myV.setBackgroundColor(Config.getStoreNavigationTextColor(context, true))

        val fm: FragmentManager = supportFragmentManager

        if (tab == 2) {
            fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.add(R.id.fragmentFL, MyStickerFragment())
            fragmentTransaction.commit()
        } else {
            fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.add(R.id.fragmentFL, AllStickerFragment())
            fragmentTransaction.commit()
        }

        allTabLL.setOnClickListener {
            changeTabs(1)

            fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentFL, AllStickerFragment())
            fragmentTransaction.commit()
        }

        myTabLL.setOnClickListener {
            changeTabs(2)

            fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentFL, MyStickerFragment())
            fragmentTransaction.commit()
        }

        changeTabs(tab)

    }

    private fun changeTabs(type: Int) {
        allTV.setTextColor(Config.getStoreNavigationTextColor(context, false))
        myTV.setTextColor(Config.getStoreNavigationTextColor(context, false))

        allV.visibility = View.INVISIBLE
        myV.visibility = View.INVISIBLE

        if (type == 1) {
            allTV.setTextColor(Config.getStoreNavigationTextColor(context, true))
            allV.visibility = View.VISIBLE
        } else {
            myTV.setTextColor(Config.getStoreNavigationTextColor(context, true))
            myV.visibility = View.VISIBLE
        }
    }

}