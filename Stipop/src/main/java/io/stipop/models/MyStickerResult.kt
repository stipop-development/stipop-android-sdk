package io.stipop.models

import java.lang.Exception

sealed class MyStickerResult {
    data class Success(val data: List<StickerPackage>) : MyStickerResult()
    data class Error(val error: Exception) : MyStickerResult()
}
