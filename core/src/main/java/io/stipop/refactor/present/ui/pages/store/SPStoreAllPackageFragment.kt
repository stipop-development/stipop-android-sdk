package io.stipop.refactor.present.ui.pages.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.databinding.FragmentStoreAllPackageListBinding
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.present.ui.adapters.StorePackageAdapter
import io.stipop.refactor.present.ui.listeners.OnClickPackageListener
import io.stipop.refactor.present.ui.listeners.OnDownloadPackageListener
import io.stipop.refactor.present.ui.view_models.StoreAllPackageViewModel
import javax.inject.Inject


class SPStoreAllPackageFragment : Fragment() {
    private lateinit var _binding: FragmentStoreAllPackageListBinding

    @Inject
    internal lateinit var _viewModel: StoreAllPackageViewModel

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
                            _viewModel.onLoadMore(findLastVisibleItemPosition())
                        }
                    })
                }
                adapter = StorePackageAdapter().apply {

                }
            }
        }


        activity?.let {
            _viewModel.listChanges.observe(it) {
                Log.d(this::class.simpleName, "storeAllPackageList.size -> ${it.size}")
                (_binding.storeAllPackageList.adapter as? StorePackageAdapter)?.run {
                    itemList = it
                    notifyDataSetChanged()
                }
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
