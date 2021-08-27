package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class KeywordListBody(@SerializedName("keywordList")
                           val keywordList: List<KeywordItem>?)
