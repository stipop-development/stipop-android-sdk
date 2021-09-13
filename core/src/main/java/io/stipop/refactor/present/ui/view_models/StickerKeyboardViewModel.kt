package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.blocs.StickerKeyboardBloc
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
import javax.inject.Inject

class StickerKeyboardViewModelV1
@Inject constructor(
    private val _stickerKeyboardBloc: StickerKeyboardBloc,
) : StickerKeyboardViewModel {

    private val _packageItemChanged: MutableLiveData<SPPackageItem?> =
        MutableLiveData()
    override val packageItemChanges: LiveData<SPPackageItem?>
        get() = _packageItemChanged

    private val _stickerItemChanged: MutableLiveData<SPStickerItem> = MutableLiveData()

    override val packageItemList: LiveData<List<SPPackageItem>> = _stickerKeyboardBloc.packageItemListChanges

    override val stickerItemList: LiveData<List<SPStickerItem>> = _stickerKeyboardBloc.stickerItemListChanges

    override fun onSelectPackageItem(item: SPPackageItem?) {
        Log.d(
            this::class.simpleName, "onSelectPackage : \n" +
                    "item -> $item"
        )
        _packageItemChanged.postValue(item)
        _stickerKeyboardBloc.onLoadMoreStickerItemList(item, -1)
    }

    override fun onLoadMorePackageItemList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadMorePackageList : \n" +
                    "index -> $index"
        )
        _stickerKeyboardBloc.onLoadMorePackageItemList(index)
    }

    override fun onLoadMoreStickerItemList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadMoreStickerList : \n" +
                    "index -> $index"
        )
    }
}

interface StickerKeyboardViewModel {

    val packageItemChanges: LiveData<SPPackageItem?>

    val packageItemList: LiveData<List<SPPackageItem>>
    val stickerItemList: LiveData<List<SPStickerItem>>

    fun onSelectPackageItem(item: SPPackageItem?)
    fun onLoadMorePackageItemList(index: Int)
    fun onLoadMoreStickerItemList(index: Int)
}
