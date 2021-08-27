package io.stipop.refactor.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import io.stipop.refactor.domain.entities.KeywordListResponse
import io.stipop.refactor.domain.entities.PackageListResponse
import io.stipop.refactor.domain.repositories.SearchRepositoryProtocol
import javax.inject.Inject

@Module
class SearchRepository @Inject constructor() : SearchRepositoryProtocol {
    private var _searchKeywordList: MutableLiveData<List<SearchKeyword>> = MutableLiveData()

    val searchKeywordListChanges: LiveData<List<SearchKeyword>> get() = _searchKeywordList
    val searchKeywordList: List<SearchKeyword> get() = _searchKeywordList.value ?: listOf()

    override suspend fun stickerSearch(
        apikey: String,
        q: String,
        userId: String,
        lang: String?,
        countryCode: String?,
        limit: Int?,
        pageNumber: Int?
    ): PackageListResponse {
        TODO("Not yet implemented")
    }

    override suspend fun trendingSearchTerms(
        apikey: String,
        lang: String?,
        countryCode: String?,
        limit: Int?
    ): KeywordListResponse {
        TODO("Not yet implemented")
    }

    override suspend fun recentSearch(apikey: String, userId: String): KeywordListResponse {
        TODO("Not yet implemented")
    }
}

typealias SearchKeyword = String
