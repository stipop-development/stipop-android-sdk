package io.stipop.refactor.present.ui.pages.store

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.stipop.Stipop
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.refactor.present.ui.view_models.StoreMode
import io.stipop.refactor.present.ui.view_models.StoreViewModel
import javax.inject.Inject

class SPStoreActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityStoreBinding

    @Inject
    internal lateinit var _viewModel: StoreViewModel

    private val _storePageFragment: StorePageFragment = StorePageFragment()
    private val _myPageFragment: SPMyPageFragment = SPMyPageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Stipop.appComponent.inject(this)

        _binding = ActivityStoreBinding.inflate(layoutInflater)
        _binding.storePageTab.setOnClickListener {
            Log.d(this::class.simpleName, "allTabLL.setOnClickListener")
            _viewModel.onChangeStoreMode(StoreMode.STORE_PAGE)
        }

        _binding.myPageTab.setOnClickListener {
            Log.d(this::class.simpleName, "myTabLL.setOnClickListener")
            _viewModel.onChangeStoreMode(StoreMode.MY_PAGE)
        }

        _viewModel.storeMode.observe(this, Observer {
            onChangeStoreMode(it)
        })

        intent.getIntExtra(StoreMode.TAG, StoreMode.STORE_PAGE.rawValue).let {
            try {
                _viewModel.onChangeStoreMode(StoreMode.values()[it])
            } catch (e: Exception) {
                Log.e(this::class.simpleName, "Incorrect store mode value", e)
            }
        }

        setContentView(_binding.root)
    }

    private fun onChangeStoreMode(mode: StoreMode) {
        Log.d(this::class.simpleName, "onChangeStoreMode: mode -> $mode")

        when(mode) {
            StoreMode.STORE_PAGE -> {
                _binding.storePageTabLabel.isSelected = true
                _binding.storePageTabIndicator.isSelected = true

                _binding.myPageTabLabel.isSelected = false
                _binding.myPageTabIndicator.isSelected = false
            }
            StoreMode.MY_PAGE -> {
                _binding.storePageTabLabel.isSelected = false
                _binding.storePageTabIndicator.isSelected = false

                _binding.myPageTabLabel.isSelected = true
                _binding.myPageTabIndicator.isSelected = true
            }
        }

        when (mode) {
            StoreMode.STORE_PAGE -> {
                supportFragmentManager.beginTransaction().replace(_binding.container.id, _storePageFragment).commit()
            }
            StoreMode.MY_PAGE -> {
                supportFragmentManager.beginTransaction().replace(_binding.container.id, _myPageFragment).commit()
            }
        }
    }
}
