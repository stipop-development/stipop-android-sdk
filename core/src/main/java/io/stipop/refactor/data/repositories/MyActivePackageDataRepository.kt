package io.stipop.refactor.data.repositories

import android.util.Log
import io.stipop.Config.Companion.apikey
import io.stipop.refactor.domain.datasources.MyStickersDatasource
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.MyActivePackageRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class MyActivePackageDataRepository
@Inject constructor(
    private val _remoteDatasource: MyStickersDatasource
) : MyActivePackageRepository() {

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
        launch {
            hasLoading = coroutineContext.isActive
            _remoteDatasource.myStickerPacks(
                user.apikey,
                user.userId,
                limit,
                getPageNumber(offset, pageMap) + 1
            )
                .run {
                    body.packageList?.let {
                        if (it.isNotEmpty()) {
                            pageMap = body.pageMap
                            _listChanged.postValue(it)
                        }
                    }
                }
            cancel()
            hasLoading = coroutineContext.isActive
        }
    }
}
