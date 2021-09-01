package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class SPKeywordListResponse(
    @SerializedName("header")
    override val header: SPHeader,
    @SerializedName("body")
    override val body: SPKeywordListBody
): SPResponse<SPKeywordListBody>





