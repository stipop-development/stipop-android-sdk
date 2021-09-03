package io.stipop.refactor.present.ui.components.common

interface SPPaging {

    interface View<T> {
        val presenter: Presenter<T>?
        val itemList: List<T>

        fun onBind(presenter: Presenter<T>?)
        fun notifyCurrentPosition(index: Int)
        fun setItemList(itemList: List<T>)
    }

    interface Presenter<T> {
        val view: View<T>?

        fun onBind(view: View<T>?)
        fun onLoadMoreList(index: Int)
        fun setItemList(itemList: List<T>)
    }
}
