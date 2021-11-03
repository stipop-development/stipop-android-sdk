package io.stipop.view.viewmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.data.SpvRepository
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
    private val recentStickers: ArrayList<Sticker> = ArrayList()
    var selectedPackage: StickerPackage? = null

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
                val result = StipopApi.create().getRecentlySentStickers(Stipop.userId, 1, 20)
                if (result.header.isSuccess()) {
                    if (recentStickers.isEmpty()) {
                        recentStickers.addAll(result.body.stickerList)
                    }
                    launch(Dispatchers.Main) {
                        onSuccess(result.body.stickerList)
                    }
                }
            }
        }
    }

    fun loadFavorites(onSuccess: (data: List<Sticker>?) -> Unit) {
        taskScope.launch {
            val result = StipopApi.create().getFavoriteStickers(Stipop.userId, 1, 20)
            if (result.header.isSuccess()) {
                launch(Dispatchers.Main) {
                    onSuccess(result.body.stickerList)
                }
            }
        }
    }

    fun loadStickerPackage(
        stickerPackage: StickerPackage,
        onSuccess: (data: StickerPackage?) -> Unit
    ) {
        selectedPackage = stickerPackage
        taskScope.launch {
            when (selectedPackage?.stickers.isNullOrEmpty()) {
                true -> {
                    val result = StipopApi.create()
                        .getStickerPackage(stickerPackage.packageId, Stipop.userId)
                    if (result.header.isSuccess()) {
                        this@SpvModel.selectedPackage = result.body?.stickerPackage
                        launch(Dispatchers.Main) {
                            onSuccess(result.body?.stickerPackage)
                        }
                    }
                }
                false -> {
                    launch(Dispatchers.Main) {
                        onSuccess(selectedPackage)
                    }
                }
            }

        }
    }
}