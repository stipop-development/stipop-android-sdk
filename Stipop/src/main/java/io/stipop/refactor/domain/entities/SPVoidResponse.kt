package io.stipop.refactor.domain.entities


import com.google.gson.annotations.SerializedName

data class SPVoidResponse(
    @SerializedName("header")
    override val header: SPHeader
) : SPResponse<Void> {
    override val body: Void?
        get() = null
}


