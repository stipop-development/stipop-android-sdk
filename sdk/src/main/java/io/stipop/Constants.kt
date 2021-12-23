package io.stipop

internal object Constants {
    object KEY {
        const val ASSET_NAME = "Stipop.json"
    }

    object Tag {
        const val DETAIL = "PD"
        const val EXTERNAL = "EXTERNAL"
        const val SSV = "SEARCH"
        const val RECENT = "RECENT"
        const val FAVORITE = "FAVORITE"
    }

    object IntentKey {
        const val STARTING_TAB_POSITION = "tab"
        const val PACKAGE_ID = "packageId"
        const val ENTRANCE_POINT = "entrancePoint"
    }

    object ApiParams {
        const val ApiKey = "apikey"
        const val SMetadata = "s_meta"
        const val Platform = "platform"
        const val SizePerPage = 20
    }

    object Value {
        const val IS_SANDBOX = false
        const val SANDBOX_URL = "https://sandbox.stipop.com/v1/"
        const val SANDBOX_APIKEY = "a3e68b6c54d8132f6879d5bc2c49708d"
        const val BASE_URL = "https://messenger.stipop.io/v1/"
        const val PLATFORM = "android-sdk"
    }

    object Point {
        const val DEFAULT = "default"
        const val STORE = "store"
        const val SEARCH = "searching"
        const val TREND = "trending"
        const val MY_STICKER = "mysticker"
        const val PACKAGE_DETAIL = "detail"
        const val PICKER_VIEW = "picker"
        const val SEARCH_VIEW = "search"
        const val EXTERNAL = "external"
        const val CURATE_A = "curation_a"
        const val CURATE_B = "curation_b"
        const val NEW = "new"
    }
}