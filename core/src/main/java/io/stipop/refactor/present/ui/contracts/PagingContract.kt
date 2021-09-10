package io.stipop.refactor.present.ui.contracts

interface PagingContract<T> {

    interface View<T> {
        val presenter: Presenter<T>?
        val itemList: List<T>

        fun onBind(presenter: Presenter<T>?)
        fun notifyCurrentPosition(index: Int)
        fun setItemList(itemList: List<T>)
        fun onClickItem(item: T)
    }

    interface Presenter<T> {
        fun onLoadMoreList(index: Int)
        fun onClickedItem(item: T)
    }
}
