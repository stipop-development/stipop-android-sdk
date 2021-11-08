package io.stipop.view

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.stipop.Config
import io.stipop.Constants
import io.stipop.Utils
import io.stipop.adapter.HomeAdapter
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.NewsAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentStoreHomeBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.viewholder.delegates.StickerPackageClickDelegate
import io.stipop.view.viewmodel.StoreHomeViewModel
import io.stipop.viewholder.delegates.KeywordClickDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


internal class StoreHomeFragment : BaseFragment(), StickerPackageClickDelegate,
    KeywordClickDelegate {

    companion object {
        fun newInstance() = StoreHomeFragment()
    }

    private var binding: FragmentStoreHomeBinding? = null
    private lateinit var viewModel: StoreHomeViewModel

    //    private val storeHomeAdapter: StoreHomeAdapter by lazy { StoreHomeAdapter(this) }
    private val homeTabAdapter: HomeAdapter by lazy { HomeAdapter(this, this) }
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(this) }
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
            StoreHomeViewModel::class.java
        )

        with(binding!!) {
            homeRecyclerView.adapter = homeTabAdapter
            allStickerRecyclerView.adapter =
                newsAdapter.withLoadStateFooter(footer = MyLoadStateAdapter { newsAdapter.retry() })
            clearSearchImageView.setOnClickListener {
                searchEditText.setText("")
                Utils.hideKeyboard(requireContext())
                binding?.searchEditText?.clearFocus()
            }
            searchEditText.addTextChangedListener { viewModel.flowQuery(it.toString().trim()) }
        }
        lifecycleScope.launch { viewModel.emittedQuery.collect { value -> refreshList(value) } }
        viewModel.getHomeSources()
        viewModel.homeDataFlow.observeForever { homeTabAdapter.setInitData(it) }
        viewModel.uiState.observeForever { isSearchView ->
            binding!!.homeRecyclerView.isVisible = !isSearchView
        }
        refreshList()
        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) { newsAdapter.refresh() }
    }

    private fun refreshList(query: String? = null) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.loadsPackages(query).collectLatest {
                newsAdapter.submitData(it)
            }
        }
    }

    override fun applyTheme() {
        with(binding) {
            val drawable = this?.searchBarContainer?.background as GradientDrawable
            drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            drawable.cornerRadius = Utils.dpToPx(Config.searchbarRadius.toFloat())
            this.searchEditText.setTextColor(Config.getSearchTitleTextColor(requireContext()))
            this.searchIconIV.setImageResource(Config.getSearchbarResourceId(requireContext()))
            this.clearSearchImageView.setImageResource(Config.getEraseResourceId(requireContext()))
            this.searchIconIV.setIconDefaultsColor()
            this.clearSearchImageView.setIconDefaultsColor()
        }
    }

    override fun onPackageDetailClicked(packageId: Int, entrancePoint: String) {
        PackageDetailBottomSheetFragment.newInstance(packageId, entrancePoint)
            .showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onDownloadClicked(position: Int, stickerPackage: StickerPackage) {
        viewModel.requestDownloadPackage(stickerPackage)
    }

    override fun onKeywordClicked(keyword: String) {
        binding?.searchEditText?.setText(keyword)
        Utils.hideKeyboard(requireContext())
        binding?.searchEditText?.clearFocus()
    }
}