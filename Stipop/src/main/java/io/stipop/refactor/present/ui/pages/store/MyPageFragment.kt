package io.stipop.refactor.present.ui.pages.store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.stipop.Stipop
import io.stipop.databinding.FragmentMyPageBinding
import io.stipop.refactor.data.models.SPMyPageMode
import io.stipop.refactor.present.ui.view_models.MyPageViewModel
import javax.inject.Inject

class MyPageFragment : Fragment() {

    private lateinit var _binding: FragmentMyPageBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModel

    private val _myActivePackageListFragment: MyActivePackageListFragment = MyActivePackageListFragment()
    private val _myHiddenPackageListFragment: MyHiddenPackageListFragment = MyHiddenPackageListFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop.appComponent.inject(this)

        _binding = FragmentMyPageBinding.inflate(layoutInflater, container, false).apply {

            myPackageToggleButton.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> {
                        _viewModel.onChangeMyPackageMode(SPMyPageMode.HIDDEN)
                    }
                    false -> {
                        _viewModel.onChangeMyPackageMode(SPMyPageMode.ACTIVE)
                    }
                }
            }
        }

        activity?.let {
            _viewModel.myPageMode.observe(it) { value ->
                Log.d(this::class.simpleName, "myPageMode -> $value")

                when (value) {
                    SPMyPageMode.ACTIVE -> {
                        _viewModel.onLoadMyActivePackageList()
                    }
                    SPMyPageMode.HIDDEN -> {
                        _viewModel.onLoadMyHiddenPackageList()
                    }
                    else -> {
                        Log.e(this::class.simpleName, "incorrect mode -> $value")
                    }
                }

                _onChangeMyPageMode(value)
            }
        }
        return _binding.root
    }

    private fun _onChangeMyPageMode(mode: SPMyPageMode?) {
        when (mode) {
            SPMyPageMode.ACTIVE -> {
                childFragmentManager.beginTransaction().replace(_binding.container.id, _myActivePackageListFragment)
                    .commit()
            }
            SPMyPageMode.HIDDEN -> {
                childFragmentManager.beginTransaction().replace(_binding.container.id, _myHiddenPackageListFragment)
                    .commit()
            }
        }
    }
}
