package io.stipop.refactor.domain.entities

data class SPErrorResponse(
    val errorType: ErrorType,
): Exception(errorType.properties.errorDescription) {
    companion object {}
}

enum class ErrorType {
    NON_EXISTS_APIKEY,
    SERVER_ERROR,
}

val ErrorType.properties: ErrorResponseProperties
    get() {
        return when (this) {
            ErrorType.NON_EXISTS_APIKEY -> {
                ErrorResponseProperties(
                    errorCode = "9000",
                    errorDescription = "The API key used does not exist"
                )
            }
            ErrorType.SERVER_ERROR -> {
                ErrorResponseProperties(
                    errorCode = "1000",
                    errorDescription = "Unable to receive data from the server"
                )
            }
        }
    }

fun SPErrorResponse.Companion.fromErrorCode(errorCode: String): SPErrorResponse? {
    ErrorType.values().firstOrNull() {
        it.properties.errorCode == errorCode
    }?.let {
        return SPErrorResponse(it)
    }
    return null
}

data class ErrorResponseProperties(
    val errorCode: String, val errorDescription: String
)
