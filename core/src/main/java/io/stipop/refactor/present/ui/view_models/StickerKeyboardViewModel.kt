package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.MyActiveStickersRepository
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.repositories.StickerPackInfoRepository
import io.stipop.refactor.domain.repositories.RecentlySentStickersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class StickerKeyboardViewModelV1
@Inject constructor(
    private val _userRepository: UserRepository,
    private val _myActiveStickersRepository: MyActiveStickersRepository,
    private val _stickerPackInfoRepository: StickerPackInfoRepository,
    private val _recentlySentStickersRepository: RecentlySentStickersRepository,
) : StickerKeyboardViewModel {

    private val _selectedPackage: MutableLiveData<SPPackageItem?> =
        MutableLiveData()
    override val selectedPackage: LiveData<SPPackageItem?>
        get() = _selectedPackage

    private val _selectedSticker: MutableLiveData<SPStickerItem> = MutableLiveData()
    override val selectedSticker: LiveData<SPStickerItem?>
        get() = _selectedSticker

    override val packageList: LiveData<List<SPPackageItem>>
        get() = _myActiveStickersRepository.listChanges

    private val _stickerList: MutableLiveData<List<SPStickerItem>> = MutableLiveData()
    override val stickerList: LiveData<List<SPStickerItem>> = MediatorLiveData<List<SPStickerItem>>().apply {
        addSource(_recentlySentStickersRepository.listChanges) {
            postValue(it)
        }
        addSource(_stickerPackInfoRepository.packageItemChanges) {
            postValue(it.stickers)
        }
    }

    override fun onSelectPackage(item: SPPackageItem?) {
        Log.d(
            this::class.simpleName, "onSelectPackage : \n" +
                    "item -> $item"
        )
        _selectedPackage.postValue(item)
        runBlocking(Dispatchers.IO) {
            _userRepository.currentUser?.let { user ->
                if (item == null) {
                    _recentlySentStickersRepository.onLoadMoreList(user, "", -1)

                } else {
                    _stickerPackInfoRepository.onLoad(user, item.packageId)
                }

                if (item == null) {
                    _stickerList.postValue(_recentlySentStickersRepository.list)
                } else {
                    _stickerList.postValue(
                        _stickerPackInfoRepository.packageItem?.stickers
                    )
                }
            }
        }

    }

    override fun onLoadMorePackageList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadMorePackageList : \n" +
                    "index -> $index"
        )
        runBlocking(Dispatchers.IO) {
            _userRepository.currentUser?.let { user ->
                _myActiveStickersRepository.onLoadMoreList(user, "", index)
            }
        }

    }

    override fun onLoadMoreStickerList(index: Int) {
        Log.d(
            this::class.simpleName, "onLoadMoreStickerList : \n" +
                    "index -> $index"
        )
    }

    override fun onSelectSticker(item: SPStickerItem?) {
        Log.d(
            this::class.simpleName, "onSelectSticker : \n" +
                    "item -> $item"
        )
        _selectedSticker.postValue(item)
    }
}

interface StickerKeyboardViewModel {

    val selectedPackage: LiveData<SPPackageItem?>
    val selectedSticker: LiveData<SPStickerItem?>

    val packageList: LiveData<List<SPPackageItem>>
    val stickerList: LiveData<List<SPStickerItem>>

    fun onSelectPackage(item: SPPackageItem?)
    fun onLoadMorePackageList(index: Int)
    fun onLoadMoreStickerList(index: Int)
    fun onSelectSticker(item: SPStickerItem?)

}
