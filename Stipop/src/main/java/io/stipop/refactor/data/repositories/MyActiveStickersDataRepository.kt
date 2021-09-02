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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyActiveStickersDataRepository
@Inject constructor(
    private val _remoteDatasource: MyStickersDatasource
) : MyActiveStickersRepository {

    private var _list: ArrayList<SPPackageItem>? = null
    override val list: List<SPPackageItem>?
        get() = _list
    private val _listChanged: PublishSubject<List<SPPackageItem>> = PublishSubject.create()
    override val listChanges: Observable<List<SPPackageItem>>
        get() = _listChanged.map { _changed ->
            arrayListOf<SPPackageItem>().apply {
                addAll(_list ?: listOf())
                _changed?.let {
                    it.forEach {
                        if (contains(it)) {
                            this[this.indexOf(it)] = it
                        } else {
                            this.add(it)
                        }
                    }
                    _list = this
                }
            }
        }

    private var _pageMap: SPPageMap? = null
    override val pageMap: SPPageMap? get() = _pageMap

    override fun onLoadList(
        user: SPUser,
        keyword: String,
        offset: Int?,
        limit: Int?
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                _remoteDatasource.myStickerPacks(
                    user.apikey,
                    user.userId,
                    limit,
                    getPageNumber(offset, pageMap)
                )
                    .run {
                        body.packageList?.let {
                            _listChanged.onNext(it)
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _list?.let {
                    val _item = it[index]
                    _remoteDatasource.hideRecoverMyPack(user.apikey, user.userId, _item.packageId)

                    it.remove(_item)
                    _listChanged.onNext(it)

                    _pageMap?.let {
                        delay(300)
                        onLoadList(user, "", index)
                    }
                }

            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            }


        }
    }
}
