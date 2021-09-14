package io.stipop.refactor.present.ui.pages.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.databinding.FragmentStoreAllPackageListBinding
import io.stipop.refactor.present.ui.adapters.StorePackageAdapter
import io.stipop.refactor.present.ui.adapters.StoreTrendingPackageItemListAdapter
import io.stipop.refactor.present.ui.pages.store.SPDetailActivity.Companion.PACKAGE_ID
import io.stipop.refactor.present.ui.view_models.StorePackageViewModel
import javax.inject.Inject


class SPStorePackageFragment : Fragment() {
    private lateinit var storeTrendingPackageAdapter: StoreTrendingPackageItemListAdapter
    private lateinit var storeAllPackageAdapter: StorePackageAdapter
    private lateinit var _binding: FragmentStoreAllPackageListBinding

    private val trendingCount = 12

    @Inject
    internal lateinit var _viewModel: StorePackageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop._appComponent.inject(this)

        _binding = FragmentStoreAllPackageListBinding.inflate(layoutInflater, container, false)
        _binding.apply {
            storeAllPackageList.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            _viewModel.onLoadMore(findLastVisibleItemPosition() + trendingCount)
                        }
                    })
                }

                storeTrendingPackageAdapter = StoreTrendingPackageItemListAdapter().apply {
                    itemClick = {
                        startActivity(Intent(activity, SPDetailActivity::class.java).apply {
                            putExtra(PACKAGE_ID, it.packageId)
                        })
                    }
                }
                storeAllPackageAdapter = StorePackageAdapter().apply {
                    itemClick = {
                        startActivity(Intent(activity, SPDetailActivity::class.java).apply {
                            putExtra(PACKAGE_ID, it.packageId)
                        })
                    }
                    downloadClick = {
                        _viewModel.onDownloadPackageItem(it)
                    }
                }

                adapter = ConcatAdapter(
                    storeTrendingPackageAdapter,
                    storeAllPackageAdapter
                )
            }
        }

        activity?.let {
            _viewModel.listChanges.observe(it) {
                Log.d(this::class.simpleName, "storeAllPackageList.size -> ${it.size}")
                storeTrendingPackageAdapter.submitList(listOf(it.filterIndexed { index, item -> index < trendingCount }))
                storeAllPackageAdapter.submitList(it.filterIndexed { index, item -> index >= trendingCount })
            }
        }

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(this::class.simpleName, "onViewCreated")

        _viewModel.onLoadMore(-1)
    }
}
