package io.stipop.view.pickerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.ViewPickerViewType
import io.stipop.base.BaseFragment
import io.stipop.databinding.ViewPickerBinding
import io.stipop.view.pickerview.listener.VisibleStateListener

class StickerPickerFragment : BaseFragment() {

    internal lateinit var binding: ViewPickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            return createView(inflater, container)
        } catch(exception: Exception) {
            Stipop.trackError(exception)
            return createView(inflater, container)
        }
    }

    private fun createView(inflater: LayoutInflater, container: ViewGroup?): View{
        binding = ViewPickerBinding.inflate(inflater, container, false)
        binding.containerLL.visibility = View.GONE
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickerFragmentInit()
    }

    private fun pickerFragmentInit(){
        if(Config.getViewPickerViewType() == ViewPickerViewType.FRAGMENT) {
            Stipop.stickerPickerViewClass = StickerPickerViewClass(
                PickerViewType.CUSTOM,
                null,
                this,
                requireActivity(),
                binding
            )
        }
    }

    internal fun setDelegate(visibleDelegate: VisibleStateListener){
        Stipop.stickerPickerViewClass?.setDelegate(visibleDelegate)
    }

    internal fun isShowing(): Boolean{
        return binding.containerLL.isVisible
    }

    internal fun show() {
        Stipop.stickerPickerViewClass?.show()
    }

    internal fun dismiss(){
        Stipop.stickerPickerViewClass?.dismiss()
    }

    override fun applyTheme() {
        if(!Config.pickerViewLayoutOnKeyboard) {
            Stipop.stickerPickerViewClass?.applyTheme()
        }
    }
}