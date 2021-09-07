package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class PackageResponse(@SerializedName("header")
                           override val header: SPHeader,
                           @SerializedName("body")
                           override val body: PackageBody) : SPResponse<PackageBody>





