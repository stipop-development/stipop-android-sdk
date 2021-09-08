package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class SPKeywordListBody(@SerializedName("keywordList")
                           val keywordList: List<SPKeywordItem>?)
