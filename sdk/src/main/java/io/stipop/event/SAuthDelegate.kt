package io.stipop.event

import io.stipop.models.enums.StipopApiEnum
import retrofit2.HttpException

interface SAuthDelegate {
    fun httpException(api: StipopApiEnum, exception: HttpException)
}