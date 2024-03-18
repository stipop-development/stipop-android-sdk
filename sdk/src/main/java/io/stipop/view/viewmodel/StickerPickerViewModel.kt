package io.stipop.view.viewmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.data.SpvRepository
import io.stipop.isNullOrNotEnough
import io.stipop.models.SPSticker
import io.stipop.models.Sticker
import io.stipop.models.StickerPackage
import io.stipop.models.body.UserIdBody
import io.stipop.models.enums.StipopApiEnum
import io.stipop.s_auth.GetStickerPackageEnum
import io.stipop.s_auth.SAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException

internal class StickerPickerViewModel {

    private val taskScope = CoroutineScope(Job() + Dispatchers.IO)
    val repository = SpvRepository()
    val recentStickers: ArrayList<Sticker> = ArrayList()
    private var selectedPackage: StickerPackage? = null

    fun trackSpv() {
        taskScope.launch {
            try {
                val response = StipopApi.create().trackViewPicker(userIdBody = UserIdBody(userId = Stipop.userId))
                if (response.code() == 401) {
                    Stipop.sAuthDelegate?.httpException(StipopApiEnum.TRACK_VIEW_PICKER, HttpException((response)))
                }
            } catch (exception: Exception) {
                Stipop.trackError(exception)
            }
        }
    }

    fun loadMyPackages(): Flow<PagingData<StickerPackage>> {
        return repository.getMyStickerStream().cachedIn(taskScope)
    }

    fun saveRecent(spSticker: SPSticker) {
        val prevSticker = recentStickers.find {
            it.stickerId == spSticker.stickerId
        }
        if (prevSticker == null) {
            recentStickers.add(0, Sticker.fromSpSticker(spSticker))
        } else {
            recentStickers.remove(prevSticker)
            recentStickers.add(0, prevSticker)
        }
    }

    fun loadRecent(isClickedRequest: Boolean, onSuccess: (data: List<Sticker>) -> Unit) {
        if (recentStickers.isNotEmpty()) {
            onSuccess(recentStickers)
        } else {
            taskScope.launch {
                try {
                    repository.getRecentlySentStickers {
                        if (it.header.isSuccess() && it.body?.stickerList != null) {
                            if (recentStickers.isEmpty()) {
                                recentStickers.addAll(it.body.stickerList)
                            }
                            launch(Dispatchers.Main) {
                                onSuccess(it.body.stickerList)
                            }
                        } else {
                            launch(Dispatchers.Main) {
                                onSuccess(emptyList())
                            }
                        }
                    }
                } catch (exception: HttpException) {
                    when (exception.code()) {
                        401 -> {
                            SAuthManager.setGetRecentlySentStickersData(isClickedRequest)
                            Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_RECENTLY_SENT_STICKERS, exception)
                        }
                    }
                } catch (exception: Exception) {
                    Stipop.trackError(exception)
                }
            }
        }
    }

    fun loadFavorites(isClickedRequest: Boolean, onSuccess: (data: List<Sticker>) -> Unit) {
        taskScope.launch {
            try {
                repository.getFavorites { result ->
                    launch(Dispatchers.Main) {
                        if (result.header.isSuccess() && result.body?.stickerList != null) {
                            onSuccess(result.body.stickerList)
                        }
                    }
                }
            } catch (exception: HttpException) {
                when (exception.code()) {
                    401 -> {
                        SAuthManager.setFavoriteStickersData(isClickedRequest)
                        Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_FAVORITE_STICKERS, exception)
                    }
                }
            } catch (exception: Exception) {
                Stipop.trackError(exception)
            }
        }
    }

    fun putFavorites(spSticker: SPSticker, onSuccess: (data: SPSticker) -> Unit) {
        taskScope.launch {
            try {
                repository.putFavorite(spSticker, onSuccess = {
                    if (it.header.isSuccess()) {
                        if (spSticker.favoriteYN != "Y") {
                            spSticker.favoriteYN = "Y"
                        } else {
                            spSticker.favoriteYN = "N"
                        }
                    }
                    launch(Dispatchers.Main) {
                        onSuccess(spSticker)
                    }
                })
            } catch (exception: HttpException) {
                when (exception.code()) {
                    401 -> Stipop.sAuthDelegate?.httpException(StipopApiEnum.PUT_MY_STICKER_FAVORITE, exception)
                }
            } catch (exception: Exception) {
                Stipop.trackError(exception)
            }
        }
    }

    fun changePackageOrder(fromStickerPackage: StickerPackage, toStickerPackage: StickerPackage) =
        taskScope.launch {
            repository.requestChangePackOrder(fromStickerPackage, toStickerPackage)
        }

    fun loadStickerPackage(
        stickerPackage: StickerPackage,
        onSuccess: (data: StickerPackage) -> Unit
    ) {
        selectedPackage = stickerPackage
        taskScope.launch {
            when (selectedPackage?.stickers.isNullOrNotEnough()) {
                true -> {
                    try {
                        repository.getStickerPackage(stickerPackage.packageId, onSuccess = {
                            if (it.header.isSuccess()) {
                                it.body?.stickerPackage?.let {
                                    selectedPackage = it
                                }
                                launch(Dispatchers.Main) {
                                    onSuccess(selectedPackage!!)
                                }
                            }
                        })
                    } catch (exception: HttpException) {
                        when (exception.code()) {
                            401 -> {
                                SAuthManager.setGetStickerPackageData(GetStickerPackageEnum.STICKER_PICKER_VIEW_MODEL, stickerPackage)
                                Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_STICKER_PACKAGE, exception)
                            }
                        }
                    } catch (exception: Exception) {
                        Stipop.trackError(exception)
                    }
                }
                false -> {
                    launch(Dispatchers.Main) {
                        onSuccess(selectedPackage!!)
                    }
                }
            }
        }
    }
}