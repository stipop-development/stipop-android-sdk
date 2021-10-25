package io.stipop.view

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.stipop.Config
import io.stipop.Constants
import io.stipop.Stipop
import io.stipop.adapter.GridStickerAdapter
import io.stipop.api.StipopApi
import io.stipop.databinding.FragmentStickerPackageBinding
import io.stipop.models.body.UserIdBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PackageDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentStickerPackageBinding? = null
    private val adapter: GridStickerAdapter by lazy { GridStickerAdapter() }
    val scope = CoroutineScope(Job() + Dispatchers.IO)

    companion object {
        fun newInstance(packageId: Int, entrancePoint: String) =
            PackageDetailBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.IntentKey.PACKAGE_ID, packageId)
                    putString(Constants.IntentKey.ENTRANCE_POINT, entrancePoint)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickerPackageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTheme()
        arguments?.let {
            val packageId = it.getInt(Constants.IntentKey.PACKAGE_ID, -1)
            val entrancePoint = it.getString(Constants.IntentKey.ENTRANCE_POINT)

            binding?.recyclerView?.adapter = adapter

            scope.launch {
                StipopApi.create().trackViewPackage(
                    UserIdBody(Stipop.userId),
                    entrancePoint = entrancePoint,
                    packageId = packageId
                )
            }
        } ?: run {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun applyTheme() {
        binding?.run {
            val drawable = containerLL.background as GradientDrawable
            drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color
            contentsRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            packageNameTV.setTextColor(Config.getDetailPackageNameTextColor(requireContext()))
            backIV.setImageResource(Config.getBackIconResourceId(requireContext()))
            closeIV.setImageResource(Config.getCloseIconResourceId(requireContext()))
            backIV.setIconDefaultsColor()
            closeIV.setIconDefaultsColor()
        }
    }
}