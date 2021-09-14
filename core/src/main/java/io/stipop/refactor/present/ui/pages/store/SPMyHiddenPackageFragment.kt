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
import io.stipop.refactor.present.ui.adapters.MyHiddenPackageAdapter
import io.stipop.refactor.present.ui.view_models.MyPageViewModel
import javax.inject.Inject

class SPMyHiddenPackageFragment : Fragment() {

    private lateinit var _binding: FragmentMyHiddenPackageListBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModel

    lateinit var myHiddenPackageAdapter: MyHiddenPackageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop._appComponent.inject(this)

        myHiddenPackageAdapter = MyHiddenPackageAdapter()

        _binding = FragmentMyHiddenPackageListBinding.inflate(layoutInflater, container, false).apply {
            hiddenPackageList.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false).apply {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            Log.d(this::class.simpleName, "hiddenPackageList onScrolled")
                            _viewModel.onLoadMyHiddenPackageList(findLastVisibleItemPosition())
                        }
                    })
                }
                adapter = myHiddenPackageAdapter.apply {
                    activeClick = { item ->
                        _viewModel.onActivePackageItem(item)
                    }
                    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

                    })
                }
            }

        }

        activity?.let {
            _viewModel.myHiddenPackageListChanges.observe(it) {
                Log.d(this::class.simpleName, "hiddenPackageList.size -> ${it.size}")
                myHiddenPackageAdapter.submitList(it)
            }
        }

        return _binding.root
    }

    override fun onStart() {
        super.onStart()
        _viewModel.onLoadMyHiddenPackageList(-1)
    }
}
