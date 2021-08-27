package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class PackageResponse(@SerializedName("header")
                           val header: Header,
                           @SerializedName("body")
                           val body: PackageBody)


data class PackageBody(@SerializedName("package")
                val _package: PackageItem)


