package io.stipop.refactor.present.ui.pages.store

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.stipop.Stipop
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.refactor.present.ui.view_models.StoreMode
import io.stipop.refactor.present.ui.view_models.StoreViewModel
import javax.inject.Inject

class StoreActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityStoreBinding

    @Inject
    internal lateinit var _viewModel: StoreViewModel

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
                _binding.storePageTabLabel.isSelected = true
                _binding.storePageTabIndicator.isSelected = true
                _binding.storePageContainer.visibility = View.VISIBLE

                _binding.myPageTabLabel.isSelected = false
                _binding.myPageTabIndicator.isSelected = false
                _binding.myPageContainer.visibility = View.GONE
            }
            StoreMode.MY_PAGE -> {
                _binding.storePageTabLabel.isSelected = false
                _binding.storePageTabIndicator.isSelected = false
                _binding.storePageContainer.visibility = View.GONE

                _binding.myPageTabLabel.isSelected = true
                _binding.myPageTabIndicator.isSelected = true
                _binding.myPageContainer.visibility = View.VISIBLE

            }
        }
    }
}
