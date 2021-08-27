package io.stipop.refactor.data.models

data class SPUser(val userId: String, val country: String, val language: String, val apikey: String) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is SPUser -> {
                userId == other.userId
                        && country == other.country
                        && language == other.language
                        && apikey == other.apikey
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + apikey.hashCode()
        return result
    }
}
