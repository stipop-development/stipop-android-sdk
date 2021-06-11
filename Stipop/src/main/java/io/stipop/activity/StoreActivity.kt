package io.stipop.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import io.stipop.APIClient
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        this.context = this

        var params = JSONObject()
        // params.put("lang", Stipop.lang)
        // params.put("countryCode", Stipop.countryCode)
        APIClient.get(
            this,
            APIClient.APIPath.SEARCH_KEYWORD.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            println(response)
        }



        params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)
        APIClient.get(
            this,
            APIClient.APIPath.SEARCH.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            println(response)
        }

        val fm: FragmentManager = supportFragmentManager
        fragmentTransaction = fm.beginTransaction()
        fragmentTransaction.add(R.id.fragmentFL, AllStickerFragment())
        fragmentTransaction.commit()

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

    }

    fun changeTabs(type: Int) {
        allTV.setTextColor(ContextCompat.getColor(context, R.color.c_c6c8cf))
        myTV.setTextColor(ContextCompat.getColor(context, R.color.c_c6c8cf))

        allV.visibility = View.INVISIBLE
        myV.visibility = View.INVISIBLE

        if (type == 1) {
            allTV.setTextColor(ContextCompat.getColor(context, R.color.c_374553))
            allV.visibility = View.VISIBLE
        } else {
            myTV.setTextColor(ContextCompat.getColor(context, R.color.c_374553))
            myV.visibility = View.VISIBLE
        }
    }

}