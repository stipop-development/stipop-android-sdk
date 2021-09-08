package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class SPPackageListResponse(
    @SerializedName("header")
    override val header: SPHeader,
    @SerializedName("body")
    override val body: SPPackageListBody
): SPResponse<SPPackageListBody>


