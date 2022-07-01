package io.stipop.view.pickerview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import io.stipop.Config
import io.stipop.base.BaseFragment
import io.stipop.databinding.ViewPickerBinding
import io.stipop.view.pickerview.listener.VisibleStateListener

class StickerPickerCustomFragment : BaseFragment() {

    companion object { fun newInstance() = StickerPickerCustomFragment() }

    internal lateinit var binding: ViewPickerBinding

    private lateinit var stickerPickerViewClass: StickerPickerViewClass

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewPickerBinding.inflate(inflater, container, false)
        binding.containerLL.visibility = View.GONE
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickerFragmentInit()
    }

    private fun pickerFragmentInit(){
        if(!Config.pickerViewLayoutOnKeyboard) {
            stickerPickerViewClass = StickerPickerViewClass(
                PickerViewType.CUSTOM,
                null,
                this,
                requireActivity(),
                binding
            )
        }
    }

    internal fun setDelegate(visibleDelegate: VisibleStateListener){
        stickerPickerViewClass.setDelegate(visibleDelegate)
    }

    internal fun isShowing(): Boolean{
        return binding.containerLL.isVisible
    }

    internal fun show() {
        stickerPickerViewClass.show()
    }

    internal fun dismiss(){
        stickerPickerViewClass.dismiss()
    }

    override fun applyTheme() {
        if(!Config.pickerViewLayoutOnKeyboard) {
            stickerPickerViewClass.applyTheme()
        }
    }
}