package io.stipop.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.stipop.Config
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.fragment.MyPageFragment
import io.stipop.fragment.StorePageFragment
import io.stipop.viewModel.StoreMode
import io.stipop.viewModel.StoreViewModel

class StoreActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityStoreBinding
    private lateinit var _viewModel: StoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityStoreBinding.inflate(layoutInflater)

        _binding.storePageTab.setOnClickListener {
            Log.d(this::class.simpleName, "allTabLL.setOnClickListener")
            _viewModel.onChangeStoreMode(StoreMode.STORE_PAGE)
        }

        _binding.myPageTab.setOnClickListener {
            Log.d(this::class.simpleName, "myTabLL.setOnClickListener")
            _viewModel.onChangeStoreMode(StoreMode.MY_PAGE)
        }

        _viewModel = viewModels<StoreViewModel>().value

        _viewModel.storeMode.observe(this, Observer {
           onChangeStoreMode(it)
        })

        intent.getIntExtra("tab", 1).let {
            when(it) {
                1 -> _viewModel.onChangeStoreMode(StoreMode.STORE_PAGE)
                2 -> _viewModel.onChangeStoreMode(StoreMode.MY_PAGE)
            }
        }

        val _fragmentTransaction = supportFragmentManager.beginTransaction()
        _fragmentTransaction.replace(_binding.storePageContainer.id, StorePageFragment())
        _fragmentTransaction.replace(_binding.myPageContainer.id, MyPageFragment())
        _fragmentTransaction.commit()

        setContentView(_binding.root)
    }

    private fun onChangeStoreMode(mode: StoreMode) {
        Log.d(this::class.simpleName, "onChangeStoreMode: mode -> $mode")

        when(mode) {
            StoreMode.STORE_PAGE -> {
                _binding.storePageTabLabel.setTextColor(Config.getStoreNavigationTextColor(this, true))
                _binding.storePageTabIndicator.visibility = View.VISIBLE
                _binding.storePageContainer.visibility = View.VISIBLE

                _binding.myPageTabLabel.setTextColor(Config.getStoreNavigationTextColor(this, false))
                _binding.myPageTabIndicator.visibility = View.GONE
                _binding.myPageContainer.visibility = View.GONE
            }
            StoreMode.MY_PAGE -> {


                _binding.storePageTabLabel.setTextColor(Config.getStoreNavigationTextColor(this, false))
                _binding.storePageTabIndicator.visibility = View.GONE
                _binding.storePageContainer.visibility = View.GONE

                _binding.myPageTabLabel.setTextColor(Config.getStoreNavigationTextColor(this, true))
                _binding.myPageTabIndicator.visibility = View.VISIBLE
                _binding.myPageContainer.visibility = View.VISIBLE

            }
        }
    }
}
