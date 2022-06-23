package io.stipop.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import io.stipop.*
import io.stipop.databinding.ActivityStoreBinding
import io.stipop.adapter.StorePagerAdapter
import io.stipop.api.StipopApi
import io.stipop.base.BaseBottomSheetDialogFragment
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.body.UserIdBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class StoreView() : BaseBottomSheetDialogFragment() {

    private var index = 0
    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private var binding: ActivityStoreBinding? = null
    private val storeAdapter: StorePagerAdapter by lazy { StorePagerAdapter(requireActivity()) }

    companion object {
        fun newInstance() = StoreView()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityStoreBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        bundleInit()
        bottomSheetSetup()
        with(binding!!) {
            storeViewPager.adapter = storeAdapter
            TabLayoutMediator(storeTabLayout, storeViewPager) { tab, position ->
                when (position) {
                    StorePagerAdapter.POSITION_ALL_STICKERS -> {
                        tab.text = getString(R.string.all_stickers)
                    }
                    StorePagerAdapter.POSITION_NEW_STICKERS -> {
                        tab.text = getString(R.string.news_tab)
                    }
                    StorePagerAdapter.POSITION_MY_STICKERS -> {
                        tab.text = getString(R.string.my_stickers)
                    }
                }
            }.attach()

            storeViewPager.apply {
                isUserInputEnabled = false
                registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    }

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val userId = Stipop.userId
                        scope.launch {
                            when (position) {
                                StorePagerAdapter.POSITION_ALL_STICKERS -> {
                                    StipopApi.create().trackViewStore(UserIdBody(userId))
                                }
                                StorePagerAdapter.POSITION_NEW_STICKERS -> {
                                    StipopApi.create().trackViewNew(UserIdBody(userId))
                                }
                                StorePagerAdapter.POSITION_MY_STICKERS -> {
                                    StipopApi.create().trackViewMySticker(UserIdBody(userId))
                                }
                            }
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                    }

                })
                setCurrentItem(
                    index, false
                )
            }
        }
        PackageDownloadEvent.liveData.observe(this) {
            Toast.makeText(requireContext(), getString(R.string.download_done), Toast.LENGTH_SHORT).show()
        }
    }

    private fun bundleInit(){
        index = arguments?.getInt("index") ?: 0
    }

    private fun bottomSheetSetup(){
        frameInit()
    }

    private fun frameInit(){
        val offsetFromTop = Constants.Value.BOTTOM_SHEET_TOP_OFFSET
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = false
            expandedOffset = offsetFromTop
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun applyTheme() {
        with(binding!!) {
            container.setStipopBackgroundColor()
            dividingLine.setStipopUnderlineColor()
            storeTabLayout.setTabLayoutStyle()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.StipopBottomSheetTheme)
    }

    override fun onDestroy() {
        super.onDestroy()
        PackageDownloadEvent.onDestroy()
    }
}