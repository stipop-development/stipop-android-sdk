package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.sticker_send.RecentlySentStickersRepository
import io.stipop.refactor.domain.services.StickerSendService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class RecentlySentStickersDataRepository @Inject constructor(
    private val stickerSendService: StickerSendService,
) : RecentlySentStickersRepository {
    private var _list: List<SPStickerItem>? = null

    override val list: List<SPStickerItem>?
        get() = _list

    private val _listChanges: BehaviorSubject<List<SPStickerItem>> = BehaviorSubject.createDefault(listOf())

    override val listChanges: Observable<List<SPStickerItem>>
        get() = _listChanges.map {

            arrayListOf<SPStickerItem>().apply {
                addAll(_list ?: listOf())
                it?.forEach {
                    if (this.contains(it)) {
                        this[this.indexOf(it)] = it
                    } else {
                        this.add(it)
                    }
                }
                _list = this
            }
        }

    private var _pageMap: SPPageMap? = null

    override val pageMap: SPPageMap?
        get() = _pageMap

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
        runBlocking(Dispatchers.IO) {
            try {
                stickerSendService.recentlySentStickers(
                    user.apikey,
                    user.userId,
                    limit,
                    getPageNumber(offset, pageMap) + 1
                )
                    .run {
                        body.stickerList?.let {
                            if (it.isNotEmpty()) {
                                _pageMap = body.pageMap
                                _listChanges.onNext(it)
                            }
                        }
                    }
            } catch (e: Exception) {
                _listChanges.onNext(listOf())
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }
}