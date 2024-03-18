package io.stipop.models.enums

interface SPPriceTierInterface {
    val price: String
}

enum class SPPriceTier: SPPriceTierInterface {

    FREE {
         override val price = "$0"
         },
    TIER1 {
        override val price = "$0.99"
    },
    TIER2 {
        override val price = "$1.99"
    },
    TIER3 {
        override val price = "$1.99"
    };

    companion object {
        fun getPriceTier(price: String?): SPPriceTier? {
            return try {
                SPPriceTier.valueOf(price ?: "")
            } catch (exception: IllegalArgumentException) {
                null
            }
        }
    }
}