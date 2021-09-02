package io.stipop.refactor.present.ui.components.common

interface SPPaging<T> {

    interface View<T> {
        fun onBind(presenter: Presenter<T>?)
        fun notifyCurrentPosition(position: Int)
        fun setItemList(itemList: List<T>)
    }

    interface Presenter<T> {
        fun onBind(view: View<T>?)
        fun onLoadMoreList(offset: Int)
    }
}
