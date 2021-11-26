package io.stipop.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.stipop.Config
import io.stipop.Constants
import io.stipop.StipopUtils
import io.stipop.adapter.HomeTabAdapter
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.PackageVerticalAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentStoreHomeBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.view.viewmodel.StoreHomeViewModel
import io.stipop.viewholder.delegates.KeywordClickDelegate
import io.stipop.viewholder.delegates.StickerPackageClickDelegate
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

    private val homeTabTabAdapter: HomeTabAdapter by lazy { HomeTabAdapter(this, this) }
    private val packageVerticalAdapter: PackageVerticalAdapter by lazy {
        PackageVerticalAdapter(
            this,
            Constants.Point.TREND
        )
    }
    private var searchJob: Job? = null
    private var backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.isSearchView) {
                binding?.searchEditText?.setText("")
                StipopUtils.hideKeyboard(requireActivity())
                binding?.searchEditText?.clearFocus()
            } else {
                requireActivity().finish()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressCallback.remove()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
            StoreHomeViewModel::class.java
        )

        with(binding!!) {
            homeRecyclerView.adapter = homeTabTabAdapter
            allStickerRecyclerView.adapter = packageVerticalAdapter.withLoadStateFooter(MyLoadStateAdapter { packageVerticalAdapter.retry() })
            clearSearchImageView.setOnClickListener {
                searchEditText.setText("")
                StipopUtils.hideKeyboard(requireActivity())
                binding?.searchEditText?.clearFocus()
            }
            searchEditText.addTextChangedListener { viewModel.flowQuery(it.toString().trim()) }
        }
        lifecycleScope.launch { viewModel.emittedQuery.collect { value -> refreshList(value) } }
        viewModel.getHomeSources()
        viewModel.homeDataFlow.observeForever { homeTabTabAdapter.setInitData(it) }
        viewModel.uiState.observeForever { isSearchView ->
            binding!!.homeRecyclerView.isVisible = !isSearchView
        }
        refreshList()
        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) { packageVerticalAdapter.refresh() }
    }

    private fun refreshList(query: String? = null) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.loadsPackages(query).collectLatest {
                packageVerticalAdapter.submitData(it)
            }
        }
    }

    override fun applyTheme() {
        with(binding) {
            val drawable = this?.searchBarContainer?.background as GradientDrawable
            drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
            drawable.cornerRadius = StipopUtils.dpToPx(Config.searchbarRadius.toFloat())
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
        StipopUtils.hideKeyboard(requireActivity())
        binding?.searchEditText?.clearFocus()
    }
}