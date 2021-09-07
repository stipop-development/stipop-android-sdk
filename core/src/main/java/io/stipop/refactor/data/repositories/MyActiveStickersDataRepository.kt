package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.stipop.Config.Companion.apikey
import io.stipop.refactor.domain.datasources.MyStickersDatasource
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.MyActiveStickersRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class MyActiveStickersDataRepository
@Inject constructor(
    private val _remoteDatasource: MyStickersDatasource
) : MyActiveStickersRepository {

    override var list: List<SPPackageItem>? = null
    private val _listChanged: PublishSubject<List<SPPackageItem>> = PublishSubject.create()
    override val listChanges: Observable<List<SPPackageItem>>
        get() = _listChanged.map { _changed ->
            arrayListOf<SPPackageItem>().apply {
                addAll(list ?: listOf())
                _changed?.let {
                    it.forEach {
                        if (contains(it)) {
                            this[this.indexOf(it)] = it
                        } else {
                            this.add(it)
                        }
                    }
                    list = this
                }
            }
        }

    override var pageMap: SPPageMap? = null

    override fun onLoadList(
        user: SPUser,
        keyword: String,
        offset: Int?,
        limit: Int?
    ) {
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
                _remoteDatasource.myStickerPacks(
                    user.apikey,
                    user.userId,
                    limit,
                    getPageNumber(offset, pageMap) + 1
                )
                    .run {
                        body.packageList?.let {
                            if (it.isNotEmpty()) {
                                pageMap= body.pageMap
                                _listChanged.onNext(it)
                            }
                        }
                    }
            } catch (e: Exception) {
                _listChanged.onNext(listOf())
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }


    override fun onHiddenPackage(user: SPUser, index: Int) {
        Log.d(
            this::class.simpleName, "onHiddenPackage : \n" +
                    "apikey -> $apikey \n" +
                    "userId -> ${user.userId} \n" +
                    "index -> $index \n"
        )
        runBlocking(Dispatchers.IO) {
            try {
                list?.let {

                    val _item = it[index]
                    _remoteDatasource.hideRecoverMyPack(user.apikey, user.userId, _item.packageId)
                    _listChanged.onNext(arrayListOf<SPPackageItem>().apply {
                        addAll(it)
                        remove(_item)
                    })

                    pageMap?.let {
                        delay(300)
                        onLoadList(user, "", index)
                    }
                }

            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            } finally {

            }
        }
    }
}
