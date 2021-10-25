package io.stipop.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.base.Injection
import io.stipop.base.BaseFragment
import io.stipop.databinding.FragmentMyStickerBinding
import io.stipop.viewholder.delegates.MyStickerItemHolderDelegate
import io.stipop.custom.dragdrop.SimpleItemTouchHelperCallback
import io.stipop.models.StickerPackage
import io.stipop.adapter.MyStickerPackageAdapter
import io.stipop.adapter.MyStickerPackageLoadStateAdapter
import io.stipop.event.PackageDownloadEvent
import io.stipop.viewmodel.MyStickerViewModel
import kotlinx.android.synthetic.main.fragment_my_sticker.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyStickerFragment : BaseFragment(), MyStickerItemHolderDelegate {

    companion object {
        fun newInstance() = Bundle().let { MyStickerFragment().apply { arguments = it } }
        private const val LAST_VISIBLE_SETTING: String = "last_visible_setting"
        private const val DEFAULT_VISIBLE = true
    }

    private var binding: FragmentMyStickerBinding? = null
    private lateinit var viewModel: MyStickerViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val myStickerPackageAdapter: MyStickerPackageAdapter by lazy {
        MyStickerPackageAdapter(
            this
        )
    }
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyStickerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
            MyStickerViewModel::class.java
        )

        myStickersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                myStickerPackageAdapter.withLoadStateFooter(footer = MyStickerPackageLoadStateAdapter { myStickerPackageAdapter.retry() })
        }

        val wantVisibleSticker =
            savedInstanceState?.getBoolean(LAST_VISIBLE_SETTING) ?: DEFAULT_VISIBLE
        toggleMyStickers(wantVisibleSticker)
        initRequest(wantVisibleSticker)

        itemTouchHelper =
            ItemTouchHelper(SimpleItemTouchHelperCallback(myStickerPackageAdapter)).apply {
                attachToRecyclerView(myStickersRecyclerView)
            }

        viewModel.packageVisibilityChanged.observeForever {
            myStickerPackageAdapter.refresh()
            requireActivity().sendBroadcast(Intent().apply {
                action = "${requireContext().packageName}.RELOAD_PACKAGE_LIST_NOTIFICATION"
            })
        }

        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
            myStickerPackageAdapter.refresh()
            binding?.myStickersRecyclerView?.scrollToPosition(0)
        }
    }

    override fun applyTheme() {
        stickerVisibleToggleTextView.setTextColor(
            Config.getActiveHiddenStickerTextColor(
                requireContext()
            )
        )
        stickerVisibleToggleTextView.setBackgroundColor(
            Config.getHiddenStickerBackgroundColor(
                requireContext()
            )
        )
    }

    private fun toggleMyStickers(wantVisibleSticker: Boolean) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.loadsPackages(wantVisibleSticker).collectLatest {
                myStickerPackageAdapter.submitData(it)
            }
        }
    }

    override fun onItemClicked(packageId: Int, entrancePoint: String) {
        PackageDetailBottomSheetFragment.newInstance(packageId, entrancePoint)
            .showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onItemLongClicked(position: Int) {

    }

    override fun onVisibilityClicked(wantToVisible: Boolean, packageId: Int, position: Int) {
        viewModel.hideOrRecoverPackage(packageId, position)
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onDragCompleted(fromData: Any, toData: Any) {
        viewModel.changePackageOrder(fromData as StickerPackage, toData as StickerPackage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(
            LAST_VISIBLE_SETTING,
            binding?.stickerVisibleToggleTextView?.isSelected ?: true
        )
    }

    private fun initRequest(wantVisibleSticker: Boolean) {
        stickerVisibleToggleTextView.isSelected = wantVisibleSticker

        stickerVisibleToggleTextView.setOnClickListener {
            stickerVisibleToggleTextView.isSelected = !stickerVisibleToggleTextView.isSelected
            when (stickerVisibleToggleTextView.isSelected) {
                true -> {
                    stickerVisibleToggleTextView.text = getString(R.string.view_hidden_stickers)
                    stickerVisibleToggleTextView.setBackgroundColor(
                        Config.getHiddenStickerBackgroundColor(
                            requireContext()
                        )
                    )
                }
                false -> {
                    stickerVisibleToggleTextView.text = getString(R.string.view_active_stickers)
                    stickerVisibleToggleTextView.setBackgroundColor(
                        Config.getActiveStickerBackgroundColor(
                            requireContext()
                        )
                    )
                }
            }
            toggleMyStickers(stickerVisibleToggleTextView.isSelected)
        }
    }

    private fun showEmptyList(show: Boolean) {
        emptyTextView.isVisible = show
        myStickersRecyclerView.isVisible = !show
    }

    private fun setNoResultView() {
        if (myStickerPackageAdapter.itemCount > 0) {
            listLL.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        } else {
            listLL.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        }
    }
//
//    fun showConfirmAlert(packageId: Int, position: Int) {
//        val customSelectProfilePicBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
//
//        val layoutBottomSheetView = this.layoutInflater.inflate(R.layout.bottom_alert, null)
//
//        val drawable = layoutBottomSheetView.findViewById<LinearLayout>(R.id.containerLL).background as GradientDrawable
//        drawable.setColor(Config.getAlertBackgroundColor(requireContext())) // solid  color
//
//        layoutBottomSheetView.findViewById<TextView>(R.id.titleTV).setTextColor(Config.getAlertTitleTextColor(requireContext()))
//        layoutBottomSheetView.findViewById<TextView>(R.id.contentsTV).setTextColor(Config.getAlertContentsTextColor(requireContext()))
//
//        val cancelTV = layoutBottomSheetView.findViewById<TextView>(R.id.cancelTV)
//        val hideTV = layoutBottomSheetView.findViewById<TextView>(R.id.hideTV)
//
//        cancelTV.setTextColor(Config.getAlertButtonTextColor(requireContext()))
//        hideTV.setTextColor(Config.getAlertButtonTextColor(requireContext()))
//
//        cancelTV.setOnClickListener {
//            customSelectProfilePicBottomSheetDialog.dismiss()
//        }
//
//        hideTV.setOnClickListener {
//            hidePackage(packageId, position)
//            customSelectProfilePicBottomSheetDialog.dismiss()
//        }
//
//        customSelectProfilePicBottomSheetDialog.setContentView(layoutBottomSheetView)
//        customSelectProfilePicBottomSheetDialog.show()
//    }
}