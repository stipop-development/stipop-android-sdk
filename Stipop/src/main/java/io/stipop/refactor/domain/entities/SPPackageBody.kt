package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class PackageBody(@SerializedName("package")
                       val _package: SPPackageItem)
