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
        fun onShow()
        fun onDismiss()
    }
}

class StipopPresenter : StipopContract.Presenter {
    var _view: StipopContract.View? = null

    override fun onBind(view: StipopContract.View?) {
        _view = view
    }

    override fun willShow() {
        Log.d(this::class.simpleName, "willShow")
    }

    override fun onShow() {
        Log.d(this::class.simpleName, "onShow")
        _view?.onShow()
    }

    override fun didShow() {
        Log.d(this::class.simpleName, "didShow")

    }

    override fun willDismiss() {
        Log.d(this::class.simpleName, "willDismiss")
    }

    override fun onDismiss() {
        Log.d(this::class.simpleName, "onDismiss")
        _view?.onDismiss()
    }

    override fun didDismiss() {
        Log.d(this::class.simpleName, "didDismiss")
    }
}
