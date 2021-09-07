package io.stipop.refactor.present.ui.pages.store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.databinding.FragmentMyHiddenPackageListBinding
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.present.ui.adapters.MyHiddenPackageAdapter
import io.stipop.refactor.present.ui.listeners.OnActivePackageListener
import io.stipop.refactor.present.ui.view_models.MyPageViewModel
import javax.inject.Inject

class SPMyHiddenPackageListFragment : Fragment() {

    private lateinit var _binding: FragmentMyHiddenPackageListBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop.appComponent.inject(this)

        _binding = FragmentMyHiddenPackageListBinding.inflate(layoutInflater, container, false).apply {
            hiddenPackageList.layoutManager = LinearLayoutManager(context)
            hiddenPackageList.adapter = MyHiddenPackageAdapter().apply {
                onActivePackageListener = object : OnActivePackageListener {
                    override fun onActive(item: SPPackage) {
                        _viewModel.onActivePackage(item)
                    }
                }
            }
            hiddenPackageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.d(this::class.simpleName, "hiddenPackageList onScrolled")
                    _viewModel.onLoadMyHiddenPackageList()
                }
            })
        }

        activity?.let {
            _viewModel.myHiddenPackageList.observe(it) { value ->
                Log.d(this::class.simpleName, "hiddenPackageList.size -> ${value.size}")
                with(_binding.hiddenPackageList.adapter as? MyHiddenPackageAdapter) {
                    this?.setItemList(value)
                }
            }

        }
        return _binding.root
    }
}
