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

    override fun onLoadList(user: SPUser, keyword: String, offset: Int?, limit: Int?) {
        Log.d(
            this::class.simpleName, "onLoadList : \n " +
                    "user -> $user \n" +
                    "keyword -> $keyword \n" +
                    "offset -> $offset \n" +
                    "limit -> $limit \n" +
                    "pageNumber -> ${getPageNumber(offset, pageMap)} \n" +
                    ""
        )
        launch {
            try {
                hasLoading = coroutineContext.isActive
                stickerSendService.recentlySentStickers(
                    user.apikey,
                    user.userId,
                    limit,
                    getPageNumber(offset, pageMap) + 1
                )
                    .run {
                        body.stickerList?.let {
                            pageMap = body.pageMap
                            _listChanged.postValue(it)
                        }
                    }
            } catch (e: Exception) {
                _listChanged.postValue(listOf())
            } finally {
                cancel()
                hasLoading = coroutineContext.isActive
            }
        }
    }
}
