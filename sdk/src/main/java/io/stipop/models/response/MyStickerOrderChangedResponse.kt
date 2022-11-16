package io.stipop.models.response

import io.stipop.models.StickerPackage

data class MyStickerOrderChangedResponse(val header: ResponseHeader, val body: ResponseBody){
    data class ResponseBody(val packageList: List<StickerPackage>)
}
