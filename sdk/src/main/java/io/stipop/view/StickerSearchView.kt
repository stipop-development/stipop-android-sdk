package io.stipop.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.stipop.*
import io.stipop.Config
import io.stipop.Constants
import io.stipop.StipopUtils
import io.stipop.adapter.HomeTabAdapter
import io.stipop.adapter.StickerDefaultAdapter
import io.stipop.adapter.PagingStickerAdapter
import io.stipop.base.Injection
import io.stipop.databinding.FragmentSearchViewBinding
import io.stipop.models.SPSticker
import io.stipop.view.viewmodel.SsvModel
import io.stipop.event.KeywordClickDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StickerSearchView : BottomSheetDialogFragment(),
    StickerDefaultAdapter.OnStickerClickListener,
    KeywordClickDelegate {

    private var binding: FragmentSearchViewBinding? = null
    private lateinit var viewModel: SsvModel
    private var searchJob: Job? = null
    private val stickerAdapter: PagingStickerAdapter by lazy { PagingStickerAdapter(this) }
    private val keywordsAdapter: HomeTabAdapter by lazy { HomeTabAdapter(null, this) }

    companion object {
        fun newInstance() = StickerSearchView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.StipopBottomSheetTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTheme()
        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(owner = this)
        ).get(SsvModel::class.java)

        with(binding!!) {
            keywordRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = keywordsAdapter
            }
            recyclerView.apply {
                layoutManager = GridLayoutManager(context, Config.searchNumOfColumns)
                adapter = stickerAdapter
            }
            clearSearchImageView.setOnClickListener {
                searchEditText.setText("")
                StipopUtils.hideKeyboard(requireActivity())
                binding?.searchEditText?.clearFocus()
            }
            searchEditText.addTextChangedListener { viewModel.flowQuery(it.toString().trim()) }
        }
        lifecycleScope.launch {
            viewModel.emittedQuery.collect { value ->
                refreshList(value)
            }
        }
        viewModel.homeDataFlow.observeForever { keywordsAdapter.setInitData(it) }
        if (!Config.searchTagsHidden) {
            viewModel.getKeywords()
        }
    }

    private fun refreshList(query: String? = null) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            stickerAdapter.submitData(PagingData.empty())
            viewModel.loadStickers(query).collectLatest {
                stickerAdapter.submitData(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog: BottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout =
            bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.isDraggable = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.peekHeight = getBottomSheetDialogDefaultHeight()
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> bottomSheet.layoutParams.height =
                        behavior.peekHeight
                    BottomSheetBehavior.STATE_COLLAPSED -> bottomSheet.layoutParams.height =
                        behavior.peekHeight
                    BottomSheetBehavior.STATE_HIDDEN -> dismiss()
                    else -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return StipopUtils.getScreenHeight(requireActivity()) * 90 / 100
    }


    private fun applyTheme() {
        with(binding!!) {
            val drawable = containerLL.background as GradientDrawable
            drawable.setColor(Color.parseColor(Config.themeBackgroundColor))

            val drawable2 = searchBarContainer.background as GradientDrawable
            drawable2.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color
            drawable2.cornerRadius = StipopUtils.dpToPx(Config.searchbarRadius.toFloat())

            searchIV.setImageResource(Config.getSearchbarResourceId(requireContext()))
            clearSearchImageView.setImageResource(Config.getEraseResourceId(requireContext()))

            searchEditText.setTextColor(Config.getSearchTitleTextColor(requireContext()))

            searchIV.setIconDefaultsColor()
            clearSearchImageView.setIconDefaultsColor()

            keywordRecyclerView.isVisible = !Config.searchTagsHidden
        }
    }

    override fun onStickerClick(position: Int, spSticker: SPSticker) {
        Stipop.send(
            spSticker.stickerId,
            spSticker.keyword,
            Constants.Point.SEARCH_VIEW
        ) { result ->
            if (result) {
                Stipop.instance?.delegate?.onStickerSelected(spSticker)
                dismiss()
            }
        }
    }

    override fun onKeywordClicked(keyword: String) {
        binding?.searchEditText?.setText(keyword)
        StipopUtils.hideKeyboard(requireActivity())
        binding?.searchEditText?.clearFocus()
    }
}