package io.stipop.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.PagingMyPackAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.custom.DragAndDropHelperCallback
import io.stipop.databinding.FragmentMyStickerBinding
import io.stipop.event.MyPackEventDelegate
import io.stipop.event.PackageDownloadEvent
import io.stipop.event.PackageVisibilityChangeEvent
import io.stipop.models.StickerPackage
import io.stipop.view.viewmodel.StoreMyStickerViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class StoreMyStickerFragment : BaseFragment(), MyPackEventDelegate {

    companion object {
        fun newInstance() = Bundle().let { StoreMyStickerFragment().apply { arguments = it } }
        private const val LAST_VISIBLE_SETTING: String = "last_visible_setting"
        private const val DEFAULT_VISIBLE = true
    }

    private var binding: FragmentMyStickerBinding? = null
    private lateinit var viewModelStore: StoreMyStickerViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val pagingMyPackAdapter: PagingMyPackAdapter by lazy { PagingMyPackAdapter(PagingMyPackAdapter.ViewType.STORE, this)
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
        viewModelStore =
            ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
                StoreMyStickerViewModel::class.java
            )

        with(binding!!) {
            myStickersRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter =
                    pagingMyPackAdapter.withLoadStateFooter(footer = MyLoadStateAdapter { pagingMyPackAdapter.retry() })
            }
            itemTouchHelper =
                ItemTouchHelper(DragAndDropHelperCallback(pagingMyPackAdapter)).apply {
                    attachToRecyclerView(myStickersRecyclerView)
                }
        }

        val wantVisibleSticker =
            savedInstanceState?.getBoolean(LAST_VISIBLE_SETTING) ?: DEFAULT_VISIBLE
        toggleMyStickers(wantVisibleSticker)
        initRequest(wantVisibleSticker)

        viewModelStore.packageVisibilityChanged.observeForever {
            pagingMyPackAdapter.refresh()
            PackageVisibilityChangeEvent.publishEvent(it.second)
        }

        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
            pagingMyPackAdapter.refresh()
            binding?.myStickersRecyclerView?.scrollToPosition(0)
        }
    }

    override fun applyTheme() {
        binding?.stickerVisibleToggleTextView?.setTextColor(
            Config.getActiveHiddenStickerTextColor(
                requireContext()
            )
        )
        binding?.stickerVisibleToggleTextView?.setBackgroundColor(
            Config.getHiddenStickerBackgroundColor(
                requireContext()
            )
        )
    }

    private fun toggleMyStickers(wantVisibleSticker: Boolean) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModelStore.loadsPackages(wantVisibleSticker).collectLatest {
                pagingMyPackAdapter.submitData(it)
            }
        }
    }

    override fun onPackageClick(position: Int, stickerPackage: StickerPackage) {
        //
    }

    override fun onItemClicked(packageId: Int, entrancePoint: String) {
        PackDetailFragment.newInstance(packageId, entrancePoint)
            .showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onItemLongClicked(position: Int) {

    }

    override fun onVisibilityClicked(wantToVisible: Boolean, packageId: Int, position: Int) {
        viewModelStore.hideOrRecoverPackage(packageId, position)
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onDragCompleted(fromData: Any, toData: Any) {
        viewModelStore.changePackageOrder(fromData as StickerPackage, toData as StickerPackage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(
            LAST_VISIBLE_SETTING,
            binding?.stickerVisibleToggleTextView?.isSelected ?: true
        )
    }

    private fun initRequest(wantVisibleSticker: Boolean) {
        binding?.stickerVisibleToggleTextView?.isSelected = wantVisibleSticker
        binding?.stickerVisibleToggleTextView?.setOnClickListener {
            binding?.stickerVisibleToggleTextView?.isSelected =
                (binding?.stickerVisibleToggleTextView?.isSelected == false)
            when (binding?.stickerVisibleToggleTextView?.isSelected) {
                true -> {
                    binding?.stickerVisibleToggleTextView?.text = getString(R.string.view_hidden_stickers)
                    binding?.stickerVisibleToggleTextView?.setBackgroundColor(
                        Config.getHiddenStickerBackgroundColor(
                            requireContext()
                        )
                    )
                }
                false -> {
                    binding?.stickerVisibleToggleTextView?.text = getString(R.string.view_active_stickers)
                    binding?.stickerVisibleToggleTextView?.setBackgroundColor(
                        Config.getActiveStickerBackgroundColor()
                    )
                }
            }
            toggleMyStickers(binding?.stickerVisibleToggleTextView?.isSelected == true)
        }
    }
}