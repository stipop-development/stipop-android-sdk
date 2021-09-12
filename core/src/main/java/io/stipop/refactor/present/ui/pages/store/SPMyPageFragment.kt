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
import io.stipop.refactor.present.ui.view_models.MyPageViewModelV1
import javax.inject.Inject

class SPMyPageFragment : Fragment() {

    private lateinit var _binding: FragmentMyPageBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModelV1

    private val _myActivePackageListFragment: SPMyActivePackageFragment = SPMyActivePackageFragment()
    private val _myHiddenPackageListFragment: SPMyHiddenPackageFragment = SPMyHiddenPackageFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop._appComponent.inject(this)

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
