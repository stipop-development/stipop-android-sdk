package io.stipop.view.viewmodel

import android.util.Log
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.body.UserIdBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class KeyboardViewModel {

    val scope = CoroutineScope(Job() + Dispatchers.IO)

    fun trackSpv() {
        scope.launch {
            StipopApi.create().trackViewPicker(UserIdBody(Stipop.userId))
        }
    }

    fun loadRecent() {
        Log.d("STIPOP-DEBUG", "RECENT RESULT")
        scope.launch {
            val result = StipopApi.create().getRecentlySentStickers(Stipop.userId, 1, 20)
            Log.d("STIPOP-DEBUG", "RECENT RESULT : ${result.header.isSuccess()}")
        }
    }

    fun loadFavorites() {
        Log.d("STIPOP-DEBUG", "FAVORITE RESULT")
        scope.launch {
            val result = StipopApi.create().getFavoriteStickers(Stipop.userId, 1, 20)
            Log.d("STIPOP-DEBUG", "FAVORITE RESULT : ${result.header.isSuccess()}")
        }
    }

    fun loadMyPackages() {
        Log.d("STIPOP-DEBUG", "MY PACKAGES RESULT")
        scope.launch {
            val result = StipopApi.create().getMyStickers(Stipop.userId, 1, 20)
            Log.d("STIPOP-DEBUG", "MY PACKAGES RESULT : ${result.header.isSuccess()}")
        }
    }

    fun loadStickerPackage(packageId: Int) {
        Log.d("STIPOP-DEBUG", "loadStickerPackage")
        scope.launch {
            val result = StipopApi.create().getStickerPackage(packageId, Stipop.userId)
            Log.d("STIPOP-DEBUG", "loadStickerPackage : ${result.header.isSuccess()}")
        }
    }
}