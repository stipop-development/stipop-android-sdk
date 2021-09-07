package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class SPPackageListBody(@SerializedName("pageMap")
                           val pageMap: SPPageMap,
                             @SerializedName("packageList")
                           val packageList: List<SPPackageItem>?)
