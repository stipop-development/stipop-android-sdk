package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class PackageListBody(@SerializedName("pageMap")
                           val pageMap: PageMap,
                           @SerializedName("packageList")
                           val packageList: List<PackageItem>?)
