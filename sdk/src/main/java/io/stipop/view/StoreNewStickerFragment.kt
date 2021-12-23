package io.stipop.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.stipop.Constants
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.PagingPackageAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentNewStickerBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.view.viewmodel.StoreNewsViewModel
import io.stipop.event.PackClickDelegate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class StoreNewStickerFragment : BaseFragment(), PackClickDelegate {

    companion object {
        fun newInstance() = StoreNewStickerFragment()
    }

    private var binding: FragmentNewStickerBinding? = null
    private lateinit var viewModel: StoreNewsViewModel
    private val pagingPackageAdapter: PagingPackageAdapter by lazy { PagingPackageAdapter(this, Constants.Point.NEW) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewStickerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PackageDownloadEvent.onDestroy()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
            StoreNewsViewModel::class.java
        )
        with(binding!!) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = pagingPackageAdapter.withLoadStateFooter(footer = MyLoadStateAdapter { pagingPackageAdapter.retry() })
            }
        }
        lifecycleScope.launch {
            viewModel.loadsPackages().collectLatest {
                pagingPackageAdapter.submitData(it)
            }
        }
        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
            pagingPackageAdapter.refresh()
        }
    }

    override fun applyTheme() {

    }

    override fun onPackageDetailClicked(packageId: Int, entrancePoint: String) {
        PackDetailFragment.newInstance(packageId, entrancePoint)
            .showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onDownloadClicked(position: Int, stickerPackage: StickerPackage) {
        viewModel.requestDownloadPackage(stickerPackage)
    }
}