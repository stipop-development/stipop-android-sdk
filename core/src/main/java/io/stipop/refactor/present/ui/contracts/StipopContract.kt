package io.stipop.refactor.present.ui.contracts

import android.util.Log

interface StipopContract {

    interface View {
        val presenter: Presenter?
        val isShow: Boolean
        fun onShow()
        fun onDismiss()
    }

    interface Presenter {
        fun onBind(view: View?)
        fun willShow()
        fun didShow()
        fun willDismiss()
        fun didDismiss()
    }
}
