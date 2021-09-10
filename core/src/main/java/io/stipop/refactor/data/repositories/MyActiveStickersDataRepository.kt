package io.stipop.refactor.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.Config.Companion.apikey
import io.stipop.refactor.domain.datasources.MyStickersDatasource
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.MyActiveStickersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class MyActiveStickersDataRepository
@Inject constructor(
    private val _remoteDatasource: MyStickersDatasource
) : MyActiveStickersRepository() {

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
            _remoteDatasource.myStickerPacks(
                user.apikey,
                user.userId,
                limit,
                getPageNumber(offset, pageMap) + 1
            )
                .let { response ->
                    response.body.let { body ->
                        body.packageList.let { packageList ->
                            pageMap = body.pageMap
                            _listChanged.postValue(packageList ?: listOf())
                        }
                    }
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
                    _listChanged.postValue(arrayListOf<SPPackageItem>().apply {
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
