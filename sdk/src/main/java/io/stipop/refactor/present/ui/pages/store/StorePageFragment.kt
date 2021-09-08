package io.stipop.refactor.present.ui.pages.store

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.stipop.Stipop
import io.stipop.databinding.FragmentStorePageBinding
import io.stipop.refactor.present.ui.view_models.StorePageMode
import io.stipop.refactor.present.ui.view_models.StorePageViewModel
import javax.inject.Inject


class StorePageFragment : Fragment() {
    private lateinit var _binding: FragmentStorePageBinding

    @Inject
    internal lateinit var _viewModel: StorePageViewModel

    private val _searchPackageListFragment: StoreSearchPackageListFragment = StoreSearchPackageListFragment()
    private val _allPackageListFragment: StoreAllPackageListFragment = StoreAllPackageListFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop.appComponent.inject(this)

        _binding = FragmentStorePageBinding.inflate(layoutInflater, container, false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(this::class.simpleName, "onViewCreated")

        _binding.searchBar.apply {
            setOnClickSearchDeleteButtonListener {
                _viewModel.onChangeStorePageMode(StorePageMode.ALL)
            }
            setOnFocusChangeSearchKeywordListener { v, hasFocus ->
                if (hasFocus) {
                    _viewModel.onChangeStorePageMode(StorePageMode.SEARCH)
                }
            }
            addTextChangedSearchKeywordListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    _viewModel.onChangeSearchKeyword("$p0")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }

        activity?.let {
            _viewModel.storePageMode.observe(it) { value ->
                Log.d(this::class.simpleName, "storeMode -> $value")
                _onChangeStorePageMode(value)
            }
        }
    }

    private fun _onChangeStorePageMode(mode: StorePageMode) {
        Log.d(this::class.simpleName, "onChangeStoreMode")
        when (mode) {
            StorePageMode.ALL -> {
                childFragmentManager.beginTransaction().replace(_binding.container.id, _allPackageListFragment).commit()
            }
            StorePageMode.SEARCH -> {
                childFragmentManager.beginTransaction().replace(_binding.container.id, _searchPackageListFragment).commit()
            }
        }
    }
}
