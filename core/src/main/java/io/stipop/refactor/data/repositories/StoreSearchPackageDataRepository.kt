package io.stipop.refactor.data.repositories

import android.util.Log
import io.stipop.refactor.data.datasources.StickerStoreRestDatasource
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.StoreSearchPackageRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreSearchPackageDataRepository @Inject constructor(
    private val _remoteDatasource: StickerStoreRestDatasource
) : StoreSearchPackageRepository() {
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
            hasLoading = coroutineContext.isActive
            _remoteDatasource.trendingStickerPacks(
                user.apikey,
                keyword,
                user.userId,
                user.language,
                user.country,
                null,
                limit,
                getPageNumber(offset, pageMap) + 1,
                null
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
