package io.stipop.refactor.domain.repositories.search

import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.repositories.PagingRepository

interface TrendingSearchTermsRepository : PagingRepository<SPKeywordItem> {

}
