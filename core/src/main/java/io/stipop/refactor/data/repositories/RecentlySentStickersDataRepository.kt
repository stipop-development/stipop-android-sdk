package io.stipop.refactor.data.repositories

import android.util.Log
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.RecentlySentStickersRepository
import io.stipop.refactor.domain.services.StickerSendService
import kotlinx.coroutines.*
import javax.inject.Inject

class RecentlySentStickersDataRepository @Inject constructor(
    private val stickerSendService: StickerSendService,
) : RecentlySentStickersRepository() {

    override fun onLoadList(user: SPUser, keyword: String, pageNumber: Int, limit: Int?) {
        Log.d(
            this::class.simpleName, "onLoadList : \n " +
                    "user -> $user \n" +
                    "keyword -> $keyword \n" +
                    "pageNumber -> $pageNumber \n" +
                    "limit -> $limit \n" +
                    ""
        )
        launch {
            try {
                hasLoading = coroutineContext.isActive
                val _response = stickerSendService.recentlySentStickers(
                    user.apikey,
                    user.userId,
                    limit,
                    pageNumber,
                )

                _response.body.let {
                    pageMap = it.pageMap
                    _listChanged.postValue(it.stickerList ?: listOf())
                }

            } catch (e: Exception) {
                _listChanged.postValue(listOf())
                Log.e(this@RecentlySentStickersDataRepository::class.simpleName, e.message, e)
            } finally {
                cancel()
                hasLoading = coroutineContext.isActive
            }
        }
    }
}
