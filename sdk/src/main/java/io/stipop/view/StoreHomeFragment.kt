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
import io.stipop.Stipop
import io.stipop.StipopUtils
import io.stipop.adapter.HomeTabAdapter
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.PagingPackageAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentStoreHomeBinding
import io.stipop.event.KeywordClickDelegate
import io.stipop.event.PackClickDelegate
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.s_auth.SHFGetTrendingStickerPackagesDelegate
import io.stipop.view.viewmodel.StoreHomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


internal class StoreHomeFragment : BaseFragment(), PackClickDelegate,
    KeywordClickDelegate, SHFGetTrendingStickerPackagesDelegate {

    companion object {
        fun newInstance() = StoreHomeFragment()
        var smfGetTrendingStickerPackagesDelegate: SHFGetTrendingStickerPackagesDelegate? = null
    }

    private var binding: FragmentStoreHomeBinding? = null

    private val homeTabAdapter: HomeTabAdapter by lazy { HomeTabAdapter(this, this) }
    private val pagingPackageAdapter: PagingPackageAdapter by lazy {
        PagingPackageAdapter(
            this,
            Constants.Point.TREND
        )
    }
    private var searchJob: Job? = null
    private var backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (Stipop.storeHomeViewModel?.uiState?.isSearchingState!!) {
                StipopUtils.hideKeyboard(requireActivity(), binding?.searchEditText)
                binding?.searchEditText?.setText("")
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
        try {
            binding = FragmentStoreHomeBinding.inflate(inflater, container, false)
            return binding!!.root
        } catch(exception: Exception){
            Stipop.trackError(exception)
            binding = FragmentStoreHomeBinding.inflate(inflater, container, false)
            return binding!!.root
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun onDestroyView() {
        StoreHomeFragment.smfGetTrendingStickerPackagesDelegate = null
        super.onDestroyView()
        backPressCallback.remove()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            StoreHomeFragment.smfGetTrendingStickerPackagesDelegate = this
            Stipop.storeHomeViewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
                StoreHomeViewModel::class.java
            )

            with(binding!!) {
                bannerRecyclerView.adapter = homeTabAdapter

                allStickerRecyclerView.adapter = pagingPackageAdapter.withLoadStateFooter(MyLoadStateAdapter { pagingPackageAdapter.retry() })
                clearSearchImageView.setOnClickListener {
                    searchEditText.setText("")
                    StipopUtils.hideKeyboard(requireActivity(), binding?.searchEditText)
                }
                searchEditText.addTextChangedListener { Stipop.storeHomeViewModel?.flowQuery(it.toString().trim()) }
            }

            lifecycleScope.launch { Stipop.storeHomeViewModel?.emittedQuery?.collect { value -> refreshList(value) } }

            Stipop.storeHomeViewModel?.run {
                getHomeSources()
                homeDataFlow.observeForever { homeTabAdapter.setInitData(it) }
                uiStateFlow.observeForever { uiState ->
                    binding!!.bannerRecyclerView.isVisible = !uiState.isSearchingState
                }
            }
            PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
                pagingPackageAdapter.refresh()
            }

            binding!!.layout.setOnTouchListener { view, motionEvent ->
                StipopUtils.hideKeyboard(requireActivity(), binding?.searchEditText)
                false
            }

            binding!!.searchEditText.setTextColor(Config.getSearchTitleTextColor(requireContext()))

            binding!!.allStickerRecyclerView.setOnTouchListener { view, motionEvent ->
                StipopUtils.hideKeyboard(requireActivity(), binding!!.searchEditText)
                false
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    private fun refreshList(query: String? = null) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            Stipop.storeHomeViewModel?.loadsPackages(query)?.collectLatest {
                pagingPackageAdapter.submitData(it)
            }
        }
    }

    override fun applyTheme() {
        try {
            with(binding) {
                (this?.searchBarContainer?.background as GradientDrawable).apply {
                    setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor))
                    cornerRadius = StipopUtils.dpToPx(Config.searchbarRadius.toFloat())
                }
                this.searchEditText.setTextColor(Config.getSearchTitleTextColor(requireContext()))
                this.searchIconIV.setImageResource(Config.getSearchbarResourceId(requireContext()))
                this.clearSearchImageView.setImageResource(Config.getEraseResourceId(requireContext()))
                this.searchIconIV.setIconDefaultsColor()
                this.clearSearchImageView.setIconDefaultsColor()
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    override fun onPackageDetailClicked(packageId: Int, entrancePoint: String) {
        PackDetailFragment.newInstance(packageId, entrancePoint).showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onDownloadClicked(position: Int, stickerPackage: StickerPackage) {
        Stipop.storeHomeViewModel?.requestDownloadPackage(stickerPackage)
    }

    override fun onKeywordClicked(keyword: String) {

        StipopUtils.hideKeyboard(requireActivity(), binding?.searchEditText)

        binding?.searchEditText?.apply {
            setText(keyword)
            clearFocus()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Stipop.storeHomeViewModel = null
    }

    override fun trendingPackageAdapterRetry() {
        pagingPackageAdapter.retry()
    }
}