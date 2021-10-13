package io.stipop.view_store

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
import io.stipop.R
import io.stipop.Utils
import io.stipop.adapter.AllStickerAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.databinding.FragmentAllStickerBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.viewholder.delegates.VerticalStickerThumbViewHolderDelegate
import io.stipop.viewmodel.AllStickerViewModel
import kotlinx.android.synthetic.main.fragment_all_sticker.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AllStickerFragment : BaseFragment(), VerticalStickerThumbViewHolderDelegate {

//    var deleteKeyword = APIClient.APIPath.SEARCH_RECENT.rawValue + "/${Stipop.userId}"
//    var getKeywords = APIClient.get(requireActivity(), APIClient.APIPath.SEARCH_KEYWORD.rawValue, null
//    var getRecentKeywords = APIClient.APIPath.SEARCH_RECENT.rawValue

    companion object {
        fun newInstance() = Bundle().apply {
        }.let { AllStickerFragment().apply { arguments = it } }
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

        binding?.allStickerRecyclerView?.adapter = allStickerAdapter
        binding?.allStickerRecyclerView?.setUpScrollListener()

        viewModel.registerRecyclerView(binding?.allStickerRecyclerView)
        viewModel.stickerPackages.observeForever { stickers ->
            allStickerAdapter.updateData(stickers)
        }

        viewModel.clearAction.observeForever { isClear ->
            if (isClear) allStickerAdapter.clearData()
        }

        lifecycleScope.launch {
            viewModel.emittedQuery.collect { value ->
                if (value.isNotEmpty()) {
                    viewModel.searchQuery(value)
                }
            }
        }

        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) { packageId ->
            Toast.makeText(context, getString(R.string.download_done), Toast.LENGTH_SHORT).show()
            allStickerAdapter.updateDownloadState(packageId)
        }

        binding?.clearSearchImageView?.setOnClickListener {
            if (binding?.searchEditText?.text?.trim().toString().isNotEmpty()) {
                searchEditText.setText("")
                viewModel.refreshData()
            }
            Utils.hideKeyboard(requireContext())
            binding?.searchEditText?.clearFocus()
        }

        binding?.searchEditText?.addTextChangedListener {
            viewModel.flowQuery(it.toString().trim())
        }
    }

    override fun applyTheme() {
        val drawable = searchBarContainer.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color
        drawable.cornerRadius = Utils.dpToPx(Config.searchbarRadius.toFloat())
        searchEditText.setTextColor(Config.getSearchTitleTextColor(requireContext()))
        searchIconIV.setImageResource(Config.getSearchbarResourceId(requireContext()))
        clearSearchImageView.setImageResource(Config.getEraseResourceId(requireContext()))
        searchIconIV.setIconDefaultsColor()
        clearSearchImageView.setIconDefaultsColor()
    }

    override fun onDownloadClicked(position: Int, stickerPackage: StickerPackage) {
        viewModel.requestDownloadPackage(stickerPackage)
    }
}