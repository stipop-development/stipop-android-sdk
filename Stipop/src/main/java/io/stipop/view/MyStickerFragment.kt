package io.stipop.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.api.APIClient
import io.stipop.api.Injection
import io.stipop.base.BaseFragment
import io.stipop.databinding.FragmentMyStickerBinding
import io.stipop.extend.dragdrop.OnViewHolderEventListener
import io.stipop.extend.dragdrop.SimpleItemTouchHelperCallback
import io.stipop.models.StickerPackage
import io.stipop.view.adapter.MyStickerAdapter
import io.stipop.view.adapter.MyStickerLoadStateAdapter
import io.stipop.viewmodel.MyStickerRepositoryViewModel
import kotlinx.android.synthetic.main.fragment_my_sticker.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class MyStickerFragment : BaseFragment(), OnViewHolderEventListener {

    companion object {
        fun newInstance() = Bundle().let { MyStickerFragment().apply { arguments = it } }
        private const val LAST_VISIBLE_SETTING: String = "last_visible_setting"
        private const val DEFAULT_VISIBLE = true
    }

    private var binding: FragmentMyStickerBinding? = null
    private lateinit var viewModel: MyStickerRepositoryViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val myStickerAdapter: MyStickerAdapter by lazy { MyStickerAdapter(this) }
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
            MyStickerRepositoryViewModel::class.java
        )

        myStickersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myStickerAdapter.withLoadStateFooter(footer = MyStickerLoadStateAdapter { myStickerAdapter.retry() })
        }

        val wantVisibleSticker = savedInstanceState?.getBoolean(LAST_VISIBLE_SETTING) ?: DEFAULT_VISIBLE
        toggleMyStickers(wantVisibleSticker)
        initRequest(wantVisibleSticker)

        itemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(myStickerAdapter)).apply { attachToRecyclerView(myStickersRecyclerView) }
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
            viewModel.loadStickers(wantVisibleSticker).collectLatest {
                myStickerAdapter.submitData(it)
            }
        }
    }

    override fun onItemClicked(position: Int) {

    }

    override fun onItemLongClicked(position: Int) {

    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onDragCompleted(fromData: Any, toData: Any) {
        Log.d("onDragCompleted", "FROM : ${(fromData as StickerPackage).packageName},, TO : ${(toData as StickerPackage).packageName}")
        viewModel.updateOrder(fromData as StickerPackage, toData as StickerPackage)
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
            myStickersRecyclerView.scrollToPosition(0)
            toggleMyStickers(stickerVisibleToggleTextView.isSelected)
        }

        lifecycleScope.launch {
            myStickerAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding?.myStickersRecyclerView?.scrollToPosition(0) }
        }
    }

    private fun showEmptyList(show: Boolean) {
        emptyTextView.isVisible = show
        myStickersRecyclerView.isVisible = !show
    }

    private fun setNoResultView() {
        if (myStickerAdapter.itemCount > 0) {
            listLL.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        } else {
            listLL.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        }
    }

//
//    fun hidePackage(packageId: Int, position: Int) {
//        val params = JSONObject()
//        APIClient.put(
//            activity as Activity,
//            APIClient.APIPath.MY_STICKER_HIDE.rawValue + "/${Stipop.userId}/$packageId",
//            params
//        ) { response: JSONObject?, e: IOException? ->
//            if (null != response) {
//                val header = response.getJSONObject("header")
//                val status = Utils.getString(header, "status")
//
//                if (status == "success") {
//                    myStickerAdapter.onItemRemove(position)
//                    setNoResultView()
//                } else {
//                    Toast.makeText(requireContext(), "ERROR!!", Toast.LENGTH_LONG).show()
//                }
//            }
//
//
//            // update keyboard package
//            val broadcastIntent = Intent()
//            broadcastIntent.action =
//                "${requireContext().packageName}.RELOAD_PACKAGE_LIST_NOTIFICATION"
//            requireContext().sendBroadcast(broadcastIntent)
//        }
//
//    }
//
//    fun showConfirmAlert(packageId: Int, position: Int) {
//        val customSelectProfilePicBottomSheetDialog =
//            BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
//
//        val layoutBottomSheetView = this.layoutInflater.inflate(R.layout.bottom_alert, null)
//
//        val drawable =
//            layoutBottomSheetView.findViewById<LinearLayout>(R.id.containerLL).background as GradientDrawable
//        drawable.setColor(Config.getAlertBackgroundColor(requireContext())) // solid  color
//
//        layoutBottomSheetView.findViewById<TextView>(R.id.titleTV)
//            .setTextColor(Config.getAlertTitleTextColor(requireContext()))
//        layoutBottomSheetView.findViewById<TextView>(R.id.contentsTV)
//            .setTextColor(Config.getAlertContentsTextColor(requireContext()))
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
//
//            customSelectProfilePicBottomSheetDialog.dismiss()
//        }
//
//        customSelectProfilePicBottomSheetDialog.setContentView(layoutBottomSheetView)
//        customSelectProfilePicBottomSheetDialog.show()
//    }
}