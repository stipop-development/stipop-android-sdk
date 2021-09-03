package io.stipop.refactor.present.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.internal.operators.single.SingleInternalHelper.toFlowable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.MyActiveStickersRepository
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.repositories.common.StickerPackInfoRepository
import io.stipop.refactor.domain.repositories.sticker_send.RecentlySentStickersRepository
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

    override val selectedPackageIndex: LiveData<Int>
        get() = _selectedPackageIndex.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    private val _packageList =
        _myActiveStickersRepository.listChanges.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    override val packageList: LiveData<List<SPPackageItem>>
        get() = _packageList


    val _stickerList = MediatorLiveData<List<SPStickerItem>>().apply {
        addSource(_selectedStickerList) {
            postValue(it)
        }

        addSource(_recentlySentStickerList) {
            postValue(it)
        }
    }

    override val stickerList: LiveData<List<SPStickerItem>>
        get() = _stickerList

    private val _recentlySentStickerList: LiveData<List<SPStickerItem>> =
        _recentlySentStickersRepository.listChanges.toFlowable(
            BackpressureStrategy.LATEST
        ).toLiveData()

    private val _selectedStickerList: LiveData<List<SPStickerItem>> =
        _stickerPackInfoRepository.packageItemChanges.map {
            it.stickers
        }.toFlowable(
            BackpressureStrategy.LATEST
        ).toLiveData()

    private val _selectedPackageIndex: BehaviorSubject<Int> = BehaviorSubject.createDefault(0).apply {
        subscribe {
            Log.d(this::class.simpleName, "[NEXT] $it")

            it?.let {
                when (it) {
                    0 -> {
                        onLoadMoreRecentlyStickerList(0)
                    }
                    else -> {

                        packageList.value?.let {

                            try {

                            } catch (e: Exception) {

                            }

                        }


                    }
                }
            }
        }
    }

    private val _currentShowPackageIndex: PublishSubject<Int> = PublishSubject.create<Int?>().apply {
        doOnNext {
            Log.d(this::class.simpleName, "[NEXT] $it")
        }
            .toFlowable(BackpressureStrategy.DROP)
            .onBackpressureDrop {
                Log.d(
                    this::class.simpleName, "[DROP] $it"
                )
            }
            .observeOn(Schedulers.io(), false, 1)
            .subscribe({ _packageIndex ->
                Log.d(this::class.simpleName, "[SUB] $_packageIndex")
                runBlocking(Dispatchers.IO) {
                    _userRepository.currentUser?.let { user ->
                        _myActiveStickersRepository.onLoadMoreList(user, "", _packageIndex)
                    }
                }
            }, {
                Log.e(this::class.simpleName, "[ERROR] ${it.message}")
            })
    }

    override fun onSelectPackage(index: Int) {
        _selectedPackageIndex.onNext(index)
    }

    override fun onLoadMorePackageList(index: Int) {
        _currentShowPackageIndex.onNext(index)
    }

    override fun onLoadMoreStickerList(index: Int) {
        _userRepository.currentUser?.let { user ->
//            _stickerPackInfoRepository.onLoad(user, )
        }
    }

    override fun onLoadMoreRecentlyStickerList(index: Int) {
        runBlocking(Dispatchers.IO) {
            _userRepository.currentUser?.let { user ->
                _recentlySentStickersRepository.onLoadList(user, "", index)
            }
        }
    }


}

interface StickerKeyboardViewModel {

    val selectedPackageIndex: LiveData<Int>
    val packageList: LiveData<List<SPPackageItem>>
    val stickerList: LiveData<List<SPStickerItem>>

    fun onSelectPackage(index: Int)
    fun onLoadMorePackageList(index: Int)
    fun onLoadMoreStickerList(index: Int)
    fun onLoadMoreRecentlyStickerList(index: Int)

}
