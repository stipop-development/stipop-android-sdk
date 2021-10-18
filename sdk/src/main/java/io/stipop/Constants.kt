package io.stipop

object Constants {
    object KEY {
        const val ASSET_NAME = "Stipop.json"
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
//        const val SANDBOX_URL = "https://sandbox.stipop.com/v1/"
        const val SANDBOX_APIKEY = "a3e68b6c54d8132f6879d5bc2c49708d"
        const val SANDBOX_URL = "https://messenger.stipop.io/v1/"
        const val BASE_URL = "https://messenger.stipop.io/v1/"
        const val PLATFORM = "android-sdk"
    }
    object Point {
        const val DEFAULT = "default" // 스토어
        const val STORE = "store"
        const val SEARCH = "searching" // 검색결과
        const val TREND = "trending" // 인기이모티콘
        const val MY_STICKER = "mysticker"
        const val PACKAGE_DETAIL = "detail"
        const val PICKER_VIEW = "picker"
        const val SEARCH_VIEW = "search"
    }
}