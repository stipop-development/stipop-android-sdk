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
import io.stipop.adapter.HomeTabAdapter
import io.stipop.adapter.PagingStickerAdapter
import io.stipop.adapter.StickerDefaultAdapter
import io.stipop.api.StipopApi
import io.stipop.base.Injection
import io.stipop.databinding.FragmentSearchViewBinding
import io.stipop.event.KeywordClickDelegate
import io.stipop.models.SPSticker
import io.stipop.models.enums.SPPriceTier
import io.stipop.models.enums.SPTapType
import io.stipop.models.response.StipopResponse
import io.stipop.s_auth.SSVAdapterReRequestDelegate
import io.stipop.s_auth.SSVOnStickerTapReRequestDelegate
import io.stipop.s_auth.TrackUsingStickerEnum
import io.stipop.view.viewmodel.SsvModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StickerSearchView : BottomSheetDialogFragment(),
    StickerDefaultAdapter.OnStickerClickListener,
    SSVOnStickerTapReRequestDelegate,
    SSVAdapterReRequestDelegate,
    KeywordClickDelegate {

    private var binding: FragmentSearchViewBinding? = null
    internal var viewModel: SsvModel? = null
    private var searchJob: Job? = null
    private val stickerAdapter: PagingStickerAdapter by lazy { PagingStickerAdapter(this, true) }
    private val keywordsAdapter: HomeTabAdapter by lazy { HomeTabAdapter(null, this) }

    companion object {
        fun newInstance() = StickerSearchView()
        var ssvOnStickerTapReRequestDelegate: SSVOnStickerTapReRequestDelegate? = null
        var ssvAdapterReRequestDelegate: SSVAdapterReRequestDelegate? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.StipopBottomSheetTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            applyTheme()
            ssvOnStickerTapReRequestDelegate = this
            ssvAdapterReRequestDelegate = this
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
                searchEditText.addTextChangedListener { viewModel!!.flowQuery(it.toString().trim()) }
            }
            lifecycleScope.launch {
                viewModel?.emittedQuery?.collect { value ->
                    refreshList(value)
                }
            }
            viewModel?.homeDataFlow?.observeForever { keywordsAdapter.setInitData(it) }
            if (!Config.searchTagsHidden) {
                viewModel?.getKeywords()
            }

            StipopUtils.hideKeyboard(requireActivity())

            binding?.recyclerView?.setOnTouchListener { view, motionEvent ->
                StipopUtils.hideKeyboard(requireActivity(), binding?.searchEditText)
                false
            }
        } catch (exception: Exception) {
            Stipop.trackError(exception)
        }
    }

    private fun refreshList(query: String? = null) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            stickerAdapter.submitData(PagingData.empty())
            viewModel?.loadStickers(query)?.collectLatest {
                stickerAdapter.submitData(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            binding = FragmentSearchViewBinding.inflate(inflater, container, false)
            return binding!!.root
        } catch (exception: Exception) {
            Stipop.trackError(exception)
            binding = FragmentSearchViewBinding.inflate(inflater, container, false)
            return binding!!.root
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        StickerSearchView.ssvOnStickerTapReRequestDelegate = null
        StickerSearchView.ssvAdapterReRequestDelegate = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return try {
            createDialog(savedInstanceState)
        } catch (exception: Exception) {
            Stipop.trackError(exception)
            createDialog(savedInstanceState)
        }
    }

    private fun createDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog: BottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        try {
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
        } catch (exception: Exception) {
            Stipop.trackError(exception)
        }
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return StipopUtils.getScreenHeight(requireActivity()) * 90 / 100
    }


    private fun applyTheme() {
        with(binding!!) {
//            val drawable = containerLL.background as GradientDrawable
//            drawable.setColor(Color.parseColor(Config.themeBackgroundColor))
            containerLL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))

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

    override fun onKeywordClicked(keyword: String) {
        binding?.searchEditText?.setText(keyword)
        StipopUtils.hideKeyboard(requireActivity())
        binding?.searchEditText?.clearFocus()
    }

    override fun onStickerSingleTap(position: Int, spSticker: SPSticker, isLockable: Boolean) {
        val priceTier = spSticker.getPriceTier()
        val isLocked = StipopUtils.isLocked(
            isLockable = isLockable,
            isDownload = spSticker.isDownload == "Y",
            priceTier = priceTier
        )

        when (isLocked) {
            true -> {
                if ((spSticker.packageId != -1) && (priceTier != null)) {
                    Stipop.stipopDelegate?.executePaymentForPackDownload(
                        priceTier = priceTier,
                        packageId = spSticker.packageId,
                        finishCallback = {
                            Stipop.mainScope.launch {
                                // 팩 구입 처리(다운로드)
                                postDownloadStickers(
                                    packageId = spSticker.packageId,
                                    priceTier = priceTier
                                ) {
                                    // Tap 이벤트 발생
                                    stickerTappedEvent(SPTapType.SINGLE, position, spSticker)
                                    // 팩 추가
                                    if (Config.getViewPickerViewType() == ViewPickerViewType.FRAGMENT) {
                                        Stipop.stickerPickerViewClass?.packAdapter?.refresh()
                                    }
                                }
                            }
                        })
                }
            }
            false -> {
                stickerTappedEvent(SPTapType.SINGLE, position, spSticker)
            }
        }
    }

    override fun onStickerDoubleTap(position: Int, spSticker: SPSticker, isLockable: Boolean) {
        val priceTier = spSticker.getPriceTier()
        val isLocked = StipopUtils.isLocked(
            isLockable = isLockable,
            isDownload = spSticker.isDownload == "Y",
            priceTier = priceTier
        )

        when (isLocked) {
            true -> {
                if ((spSticker.packageId != -1) && (priceTier != null)) {
                    Stipop.stipopDelegate?.executePaymentForPackDownload(
                        priceTier = priceTier,
                        packageId = spSticker.packageId,
                        finishCallback = {
                            Stipop.mainScope.launch {
                                // 팩 구입 처리(다운로드)
                                postDownloadStickers(
                                    packageId = spSticker.packageId,
                                    priceTier = priceTier
                                ) {
                                    // Tap 이벤트 발생
                                    stickerTappedEvent(SPTapType.DOUBLE, position, spSticker)
                                }
                            }
                        })
                }
            }
            false -> {
                stickerTappedEvent(SPTapType.DOUBLE, position, spSticker)
            }
        }
    }

    private fun stickerTappedEvent(tapType: SPTapType, position: Int, spSticker: SPSticker) {
        when (tapType) {
            SPTapType.SINGLE -> {
                Stipop.send(
                    TrackUsingStickerEnum.STICKER_SEARCH_VIEW_SINGLE_TAP,
                    spSticker,
                    Constants.Point.SEARCH_VIEW
                ) { result ->
                    if (result) {
                        Stipop.instance?.delegate?.onStickerSingleTapped(spSticker)
                        dismiss()
                    }
                }
            }
            SPTapType.DOUBLE -> {
                Stipop.send(
                    TrackUsingStickerEnum.STICKER_SEARCH_VIEW_DOUBLE_TAP,
                    spSticker,
                    Constants.Point.SEARCH_VIEW
                ) { result ->
                    if (result) {
                        Stipop.instance?.delegate?.onStickerDoubleTapped(spSticker)
                        dismiss()
                    }
                }
            }
        }
    }

    override fun ssvOnStickerSingleTapReRequest(position: Int, spSticker: SPSticker) {
        onStickerSingleTap(position, spSticker, true)
    }

    override fun ssvOnStickerDoubleTapReRequest(position: Int, spSticker: SPSticker) {
        onStickerDoubleTap(position, spSticker, true)
    }

    override fun stickerAdapterRetry() {
        stickerAdapter.retry()
    }

    override fun keywordAdapterRefresh() {
        viewModel?.getKeywords()
    }

    suspend fun <T : Any> safeCall(
        call: suspend () -> T,
        onCompletable: (data: T?) -> Unit,
    ) {
        val result = call.invoke()
        onCompletable(result)
    }

    private suspend fun postDownloadStickers(
        packageId: Int,
        priceTier: SPPriceTier,
        onSuccess: (data: StipopResponse?) -> Unit
    ) {
        val isPurchaseMode = if (Config.isPackPurchaseMode) {
            "Y"
        } else {
            "N"
        }
        val priceQueryValue = if (Config.isPackPurchaseMode) {
            priceTier?.price
        } else {
            null
        }

        safeCall(
            call = {
                StipopApi.create().postDownloadStickers(
                    packageId = packageId,
                    isPurchase = isPurchaseMode,
                    userId = Stipop.userId,
                    lang = Stipop.lang,
                    countryCode = Stipop.countryCode,
                    price = priceQueryValue,
                    entrancePoint = Constants.Point.STORE,
                    eventPoint = Constants.Point.STORE
                )
            }, onCompletable = {
                onSuccess(it)
            })
    }
}