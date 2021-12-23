package io.stipop.view.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.StipopUtils
import io.stipop.api.StipopApi
import io.stipop.data.SpvRepository
import io.stipop.isNullOrNotEnough
import io.stipop.models.SPSticker
import io.stipop.models.Sticker
import io.stipop.models.StickerPackage
import io.stipop.models.body.UserIdBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class SpvModel {

    private val taskScope = CoroutineScope(Job() + Dispatchers.IO)
    private val repository = SpvRepository(StipopApi.create())
    val recentStickers: ArrayList<Sticker> = ArrayList()
    private var selectedPackage: StickerPackage? = null

    fun trackSpv() {
        taskScope.launch {
            StipopApi.create().trackViewPicker(UserIdBody(Stipop.userId))
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

    fun loadRecent(onSuccess: (data: List<Sticker>) -> Unit) {
        if (recentStickers.isNotEmpty()) {
            onSuccess(recentStickers)
        } else {
            taskScope.launch {
                val result = StipopApi.create().getRecentlySentStickers(Stipop.userId, 1, 24)
                if (result.header.isSuccess() && result.body?.stickerList != null) {
                    if (recentStickers.isEmpty()) {
                        recentStickers.addAll(result.body.stickerList)
                    }
                    launch(Dispatchers.Main) {
                        onSuccess(result.body.stickerList)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        onSuccess(emptyList())
                    }
                }
            }
        }
    }

    fun loadFavorites(onSuccess: (data: List<Sticker>) -> Unit) {
        taskScope.launch {
            repository.getFavorites { result ->
                launch(Dispatchers.Main) {
                    if (result.header.isSuccess() && result.body?.stickerList != null) {
                        onSuccess(result.body.stickerList)
                    }
                }
            }
        }
    }

    fun putFavorites(spSticker: SPSticker, onSuccess: (data: SPSticker) -> Unit) {
        taskScope.launch {
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
                    repository.getStickerPackage(stickerPackage.packageId, onSuccess = {
                        if (it.header.isSuccess()) {
                            it.body?.stickerPackage?.let{
                                selectedPackage = it
                            }
                            launch(Dispatchers.Main) {
                                onSuccess(selectedPackage!!)
                            }
                        }
                    })
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