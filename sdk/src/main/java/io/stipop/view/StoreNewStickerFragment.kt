package io.stipop.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.PagingPackageAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentNewStickerBinding
import io.stipop.event.PackClickDelegate
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.s_auth.SNSFGetNewStickerPackagesReRequestDelegate
import io.stipop.view.viewmodel.StoreNewsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class StoreNewStickerFragment : BaseFragment(), SNSFGetNewStickerPackagesReRequestDelegate, PackClickDelegate {

    companion object {
        fun newInstance() = StoreNewStickerFragment()
        var snsfGetNewStickerPackagesReRequestDelegate: SNSFGetNewStickerPackagesReRequestDelegate? = null
    }

    private var binding: FragmentNewStickerBinding? = null
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
        Stipop.storeNewsViewModel = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StoreNewStickerFragment.snsfGetNewStickerPackagesReRequestDelegate = this
        Stipop.storeNewsViewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
            StoreNewsViewModel::class.java
        )
        with(binding!!) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = pagingPackageAdapter.withLoadStateFooter(footer = MyLoadStateAdapter { pagingPackageAdapter.retry() })
            }
        }
        loadPackages()
        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
            pagingPackageAdapter.refresh()
        }
    }

    private fun loadPackages(){
        lifecycleScope.launch {
            Stipop.storeNewsViewModel?.loadsPackages()?.collectLatest {
                pagingPackageAdapter.submitData(it)
            }
        }
    }

    override fun applyTheme() {

    }

    override fun onPackageDetailClicked(packageId: Int, entrancePoint: String) {
        PackDetailFragment.newInstance(packageId, entrancePoint)
            .showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onDownloadClicked(position: Int, stickerPackage: StickerPackage) {
        Stipop.storeNewsViewModel?.requestDownloadPackage(stickerPackage)
    }

    override fun packageAdapterRetry() {
        pagingPackageAdapter.retry()
    }
}