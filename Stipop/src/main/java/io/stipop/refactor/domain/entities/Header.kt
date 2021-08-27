package io.stipop.refactor.domain.entities

import com.google.gson.annotations.SerializedName

data class Header(@SerializedName("code")
                  val code: String = "",
                  @SerializedName("message")
                  val message: String = "",
                  @SerializedName("status")
                  val status: String = "")
