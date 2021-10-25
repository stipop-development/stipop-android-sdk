package io.stipop.view

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.Utils
import io.stipop.adapter.AllStickerAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentAllStickerBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.viewholder.delegates.StickerPackageClickDelegate
import io.stipop.viewmodel.AllStickerViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AllStickerFragment : BaseFragment(), StickerPackageClickDelegate {

//    var deleteKeyword = APIClient.APIPath.SEARCH_RECENT.rawValue + "/${Stipop.userId}"
//    var getKeywords = APIClient.get(requireActivity(), APIClient.APIPath.SEARCH_KEYWORD.rawValue, null
//    var getRecentKeywords = APIClient.APIPath.SEARCH_RECENT.rawValue

    companion object {
        fun newInstance() = AllStickerFragment()
    }

    private var binding: FragmentAllStickerBinding? = null
    private lateinit var viewModel: AllStickerViewModel
    private val allStickerAdapter: AllStickerAdapter by lazy { AllStickerAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllStickerBinding.inflate(inflater, container, false)
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
            AllStickerViewModel::class.java
        )

        with(binding) {
            this?.allStickerRecyclerView?.adapter = allStickerAdapter
            this?.allStickerRecyclerView?.setUpScrollListener()
            this?.clearSearchImageView?.setOnClickListener {
                searchEditText.setText("")
                viewModel.refreshData("")
                Utils.hideKeyboard(requireContext())
                binding?.searchEditText?.clearFocus()
            }
            this?.searchEditText?.addTextChangedListener {
                viewModel.flowQuery(it.toString().trim())
            }
        }

        viewModel.registerRecyclerView(binding?.allStickerRecyclerView)
        viewModel.stickerPackages.observeForever { stickers ->
            allStickerAdapter.updateData(stickers)
        }
        viewModel.clearAction.observeForever { isSearchView ->
            allStickerAdapter.clearData(isSearchView)
        }
        lifecycleScope.launch {
            viewModel.emittedQuery.collect { value -> viewModel.searchQuery(value) }
        }
        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) { packageId ->
            Toast.makeText(context, getString(R.string.download_done), Toast.LENGTH_SHORT).show()
            allStickerAdapter.updateDownloadState(packageId)
        }
    }

    override fun applyTheme() {
        with(binding) {
            val drawable = this?.searchBarContainer?.background as GradientDrawable
            drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color
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
}