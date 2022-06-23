package io.stipop.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Constants
import io.stipop.StipopUtils
import io.stipop.adapter.HomeTabAdapter
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.PagingPackageAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentStoreHomeBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.view.viewmodel.StoreHomeViewModel
import io.stipop.event.KeywordClickDelegate
import io.stipop.event.PackClickDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


internal class StoreHomeFragment : BaseFragment(), PackClickDelegate,
    KeywordClickDelegate {

    companion object {
        fun newInstance() = StoreHomeFragment()
    }

    private var binding: FragmentStoreHomeBinding? = null
    private lateinit var viewModel: StoreHomeViewModel

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
            if (viewModel.uiState.isSearchingState) {
                StipopUtils.hideKeyboard(requireActivity())
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
            bannerRecyclerView.adapter = homeTabAdapter

            allStickerRecyclerView.adapter = pagingPackageAdapter.withLoadStateFooter(MyLoadStateAdapter { pagingPackageAdapter.retry() })
            clearSearchImageView.setOnClickListener {
                searchEditText.setText("")
                hideKeyboard()
            }
            searchEditText.addTextChangedListener { viewModel.flowQuery(it.toString().trim()) }
        }

        lifecycleScope.launch { viewModel.emittedQuery.collect { value -> refreshList(value) } }

              viewModel.run {
                  getHomeSources()
                  homeDataFlow.observeForever { homeTabAdapter.setInitData(it) }
                  uiStateFlow.observeForever { uiState ->
                      binding?.bannerRecyclerView?.isVisible = !uiState.isSearchingState
                  }
              }
              PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
                  pagingPackageAdapter.refresh()
              }

                 binding!!.layout.setOnTouchListener { view, motionEvent ->
                     hideKeyboard()
                  false
              }

        binding!!.bannerRecyclerView.setOnTouchListener { view, motionEvent ->
            false
        }

    }
    private fun hideKeyboard(){
        binding!!.searchEditText?.let {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            it.clearFocus()
        }
    }

    private fun refreshList(query: String? = null) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.loadsPackages(query).collectLatest {
                pagingPackageAdapter.submitData(it)
            }
        }
    }

    override fun applyTheme() {
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
    }

    override fun onPackageDetailClicked(packageId: Int, entrancePoint: String) {
        PackDetailFragment.newInstance(packageId, entrancePoint)
            .showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onDownloadClicked(position: Int, stickerPackage: StickerPackage) {
        viewModel.requestDownloadPackage(stickerPackage)
    }


    override fun onKeywordClicked(keyword: String) {
        hideKeyboard()
        binding?.searchEditText?.apply {
            setText(keyword)
            clearFocus()
        }
    }
}