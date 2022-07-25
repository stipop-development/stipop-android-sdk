package io.stipop.s_auth

import android.util.Log
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.SPSticker
import io.stipop.models.StickerPackage
import io.stipop.models.body.UserIdBody
import io.stipop.models.enum.StipopApiEnum
import io.stipop.view.StickerSearchView
import io.stipop.view.StoreHomeFragment
import io.stipop.view.StoreMyStickerFragment
import io.stipop.view.StoreNewStickerFragment
import io.stipop.view.pickerview.StickerPickerViewClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface SPVRecentStickerAdapterReRequestDelegate {
    fun recentStickerAdapterRetry()
}

interface SPVGetMyStickersReRequestDelegate {
    fun getMyStickersRetry()
}

interface SHFGetTrendingStickerPackagesDelegate {
    fun trendingPackageAdapterRetry()
}

interface SMSFGetMyStickersReRequestDelegate {
    fun getMyVisibleStickersRetry()
    fun getMyHiddenStickersRetry()
}

interface SNSFGetNewStickerPackagesReRequestDelegate {
    fun packageAdapterRetry()
}

interface SSVOnStickerTapReRequestDelegate {
    fun ssvOnStickerSingleTapReRequest(position: Int, spSticker: SPSticker)
    fun ssvOnStickerDoubleTapReRequest(position: Int, spSticker: SPSticker)
}

interface SSVAdapterReRequestDelegate {
    fun stickerAdapterRetry()
    fun keywordAdapterRefresh()
}

enum class GetStickerPackageEnum{
    STICKER_PICKER_VIEW_MODEL, PACK_DETAIL_VIEW_MODEL
}

enum class GetRecommendedKeywordsEnum{
    STICKER_SEARCH_VIEW, STORE_HOME_FRAGMENT
}

enum class GetMyStickerEnum{
    STICKER_PICKER_VIEW_CLASS, STORE_MY_STICKER_FRAGMENT
}

enum class PutMyStickersOrdersEnum{
    STICKER_PICKER_VIEW_MODEL, STORE_MY_STICKER_VIEW_MODEL
}

enum class TrackUsingStickerEnum{
    STICKER_SEARCH_VIEW_SINGLE_TAP, STICKER_SEARCH_VIEW_DOUBLE_TAP, STICKER_PICKER_VIEW_CLASS_SINGLE_TAP, STICKER_PICKER_VIEW_CLASS_DOUBLE_TAP
}

enum class PostDownloadStickersEnum{
    STORE_HOME_VIEW_MODEL, STORE_NEWS_VIEW_MODEL, PACK_DETAIL_VIEW_MODEL
}

class SAuthManager {

    companion object {

        private var getStickerPackageEnum: GetStickerPackageEnum? = null
        private var getStickerPackageStickerPackage: StickerPackage? = null

        private var getRecommendedKeywordsEnum: GetRecommendedKeywordsEnum? = null

        private var getMyStickerEnum: GetMyStickerEnum? = null

        private var getRecentlySentStickersIsClickRequest: Boolean? = null

        private var favoriteStickersIsClickedRequest: Boolean? = null

        private var putMyStickersOrdersEnum: PutMyStickersOrdersEnum? = null
        private var putMyStickersOrdersFromStickerPackage: StickerPackage? = null
        private var putMyStickersOrdersToStickerPackage: StickerPackage? = null

        private var putMyStickerVisibilityPackageId: Int? = null
        private var putMyStickerVisibilityPosition: Int? = null

        private var postDownloadStickersEnum: PostDownloadStickersEnum? = null
        private var postDownloadStickersStickerPackage: StickerPackage? = null

        private var trackViewPackagePackageId: Int? = null
        private var trackViewPackageEntrancePoint: String? = null

        private var trackUsingStickerEnum: TrackUsingStickerEnum? = null
        private var trackUsingStickerSPSticker: SPSticker? = null

        internal fun setAccessToken(accessToken: String){
            StipopApi.setAccessToken(accessToken)
        }

        fun reRequest(api: StipopApiEnum){

            when (api) {
                StipopApiEnum.INIT_SDK -> initSDK()
                StipopApiEnum.GET_HOME_SOURCES -> getHomeSources()
                StipopApiEnum.GET_STICKER_PACKAGE -> getStickerPackage()
                StipopApiEnum.GET_RECOMMENDED_KEYWORDS -> getRecommendedKeywords()
                StipopApiEnum.GET_RECENTLY_SENT_STICKERS -> getRecentlySentStickers()
                StipopApiEnum.GET_FAVORITE_STICKERS -> getFavoriteStickers()
                StipopApiEnum.GET_MY_STICKERS -> getMyStickers()
                StipopApiEnum.GET_MY_HIDDEN_STICKERS -> getMyHiddenStickers()
                StipopApiEnum.PUT_MY_STICKER_FAVORITE -> putMyStickerFavorite()
                StipopApiEnum.PUT_MY_STICKERS_ORDERS -> putMyStickersOrders()
                StipopApiEnum.PUT_MY_STICKER_VISIBILITY -> putMyStickerVisibility()
                StipopApiEnum.GET_TRENDING_STICKER_PACKAGES -> getTrendingStickerPackages()
                StipopApiEnum.GET_STICKERS -> getStickers()
                StipopApiEnum.GET_NEW_STICKER_PACKAGES -> getNewStickerPackages()
                StipopApiEnum.POST_DOWNLOAD_STICKERS -> postDownloadStickers()
                StipopApiEnum.TRACK_CONFIG -> trackConfig()
                StipopApiEnum.TRACK_VIEW_PICKER -> trackViewPicker()
                StipopApiEnum.TRACK_VIEW_SEARCH -> trackViewSearch()
                StipopApiEnum.TRACK_VIEW_STORE -> trackViewStore()
                StipopApiEnum.TRACK_VIEW_NEW -> trackViewNew()
                StipopApiEnum.TRACK_VIEW_MY_STICKER -> trackViewMySticker()
                StipopApiEnum.TRACK_VIEW_PACKAGE -> trackViewPackage()
                StipopApiEnum.TRACK_USING_STICKER -> postTrackUsingSticker()
            }
        }

        private fun initSDK(){
            CoroutineScope(Dispatchers.Main).launch {
                Stipop.postInitSDK()
            }
        }

        private fun getHomeSources(){
            Stipop.storeHomeViewModel?.getHomeSources()
        }

        private fun getStickerPackage(){
            when(getStickerPackageEnum){
                GetStickerPackageEnum.STICKER_PICKER_VIEW_MODEL -> {
                    getStickerPackageStickerPackage?.let {
                        Stipop.stickerPickerViewClass?.loadStickerPackage(it)
                    }
                }
                GetStickerPackageEnum.PACK_DETAIL_VIEW_MODEL -> {
                    getStickerPackageStickerPackage?.packageId?.let {
                        Stipop.packDetailViewModel?.loadsPackages(it)
                    }
                }
            }
            setGetStickerPackageData(null, null)
        }

        internal fun setGetStickerPackageData(getStickerPackageEnum: GetStickerPackageEnum?, stickerPackage: StickerPackage?){
            this.getStickerPackageEnum = getStickerPackageEnum
            this.getStickerPackageStickerPackage = stickerPackage
        }

        private fun getRecommendedKeywords(){
            when(getRecommendedKeywordsEnum){
                GetRecommendedKeywordsEnum.STICKER_SEARCH_VIEW -> StickerSearchView.ssvAdapterReRequestDelegate?.keywordAdapterRefresh()
                GetRecommendedKeywordsEnum.STORE_HOME_FRAGMENT -> {}  // Instead work in homeSources
                else -> {}
            }
            setGetRecommendedKeywordsData(null)
        }

        internal fun setGetRecommendedKeywordsData(getRecommendedKeywordsEnum: GetRecommendedKeywordsEnum?){
            this.getRecommendedKeywordsEnum = getRecommendedKeywordsEnum
        }

        private fun getRecentlySentStickers(){
            CoroutineScope(Dispatchers.Main).launch {
                getRecentlySentStickersIsClickRequest?.let {
                    Stipop.stickerPickerViewClass?.getRecentFavorite(it)
                }
                setGetRecentlySentStickersData(false)
            }
        }

        internal fun setGetRecentlySentStickersData(isClickedRequest: Boolean?){
            this.getRecentlySentStickersIsClickRequest = isClickedRequest
        }

        private fun getFavoriteStickers(){
            favoriteStickersIsClickedRequest?.let {
                Stipop.stickerPickerViewClass?.loadFavorites(it)
            }
            setFavoriteStickersData(null)
        }

        internal fun setFavoriteStickersData(isClickedRequest: Boolean?){
            favoriteStickersIsClickedRequest = isClickedRequest
        }

        private fun getMyStickers(){
            when(getMyStickerEnum){
                GetMyStickerEnum.STICKER_PICKER_VIEW_CLASS -> StickerPickerViewClass.spvGetMyStickersReRequestDelegate?.getMyStickersRetry()
                GetMyStickerEnum.STORE_MY_STICKER_FRAGMENT -> StoreMyStickerFragment.smsfGetMyStickersReRequestDelegate?.getMyVisibleStickersRetry()
            }
            setGetMyStickersData(null)
        }

        internal fun setGetMyStickersData(getMyStickerEnum: GetMyStickerEnum?){
            this.getMyStickerEnum = getMyStickerEnum
        }

        private fun getMyHiddenStickers(){
            StoreMyStickerFragment.smsfGetMyStickersReRequestDelegate?.getMyHiddenStickersRetry()
        }

        private fun putMyStickerFavorite(){
            Stipop.stickerPickerViewClass?.stickerPickerViewPreview?.updateFavorite()
        }

        private fun putMyStickersOrders(){
            CoroutineScope(Dispatchers.IO).launch {
                if(putMyStickersOrdersFromStickerPackage != null && putMyStickersOrdersToStickerPackage != null) {
                    when(putMyStickersOrdersEnum){
                        PutMyStickersOrdersEnum.STICKER_PICKER_VIEW_MODEL -> Stipop.stickerPickerViewModel?.repository?.requestChangePackOrder(putMyStickersOrdersFromStickerPackage!!, putMyStickersOrdersToStickerPackage!!)
                        PutMyStickersOrdersEnum.STORE_MY_STICKER_VIEW_MODEL -> Stipop.storeMyStickerViewModel?.repository?.requestChangePackOrder(putMyStickersOrdersFromStickerPackage!!, putMyStickersOrdersToStickerPackage!!)
                    }
                }
                setPutMyStickersOrdersData(null, null, null)
            }
        }

        internal fun setPutMyStickersOrdersData(putMyStickersOrdersEnum: PutMyStickersOrdersEnum?, fromStickerPackage: StickerPackage?, toStickerPackage: StickerPackage?){
            this.putMyStickersOrdersEnum = putMyStickersOrdersEnum
            this.putMyStickersOrdersFromStickerPackage = fromStickerPackage
            this.putMyStickersOrdersToStickerPackage = toStickerPackage
        }

        private fun putMyStickerVisibility(){
            CoroutineScope(Dispatchers.IO).launch {
                if(putMyStickerVisibilityPackageId != null && putMyStickerVisibilityPosition != null) {
                    Stipop.storeMyStickerViewModel?.repository?.updatePackageVisibility(putMyStickerVisibilityPackageId!!, putMyStickerVisibilityPosition!!)
                }
                setPutMyStickerVisibilityData(null, null)
            }
        }

        internal fun setPutMyStickerVisibilityData(packageId: Int?, position: Int?){
            this.putMyStickerVisibilityPackageId = packageId
            this.putMyStickerVisibilityPosition = position
        }

        private fun getTrendingStickerPackages(){
            StoreHomeFragment.smfGetTrendingStickerPackagesDelegate?.trendingPackageAdapterRetry()
        }

        private fun getStickers(){
            StickerSearchView.ssvAdapterReRequestDelegate?.stickerAdapterRetry()
        }

        private fun getNewStickerPackages(){
            StoreNewStickerFragment.snsfGetNewStickerPackagesReRequestDelegate?.packageAdapterRetry()
        }

        private fun postDownloadStickers() {
            postDownloadStickersStickerPackage?.let {
                when(postDownloadStickersEnum){
                    PostDownloadStickersEnum.STORE_HOME_VIEW_MODEL -> Stipop.storeHomeViewModel?.requestDownloadPackage(it)
                    PostDownloadStickersEnum.STORE_NEWS_VIEW_MODEL -> Stipop.storeNewsViewModel?.requestDownloadPackage(it)
                    PostDownloadStickersEnum.PACK_DETAIL_VIEW_MODEL -> Stipop.packDetailViewModel?.requestDownloadPackage()
                    else -> {}
                }
            }
            setPostDownloadStickersData(null, null)
        }

        internal fun setPostDownloadStickersData(postDownloadStickersEnum: PostDownloadStickersEnum?, postDownloadStickersStickerPackage: StickerPackage?){
            this.postDownloadStickersEnum = postDownloadStickersEnum
            this.postDownloadStickersStickerPackage = postDownloadStickersStickerPackage
        }

        private fun trackConfig() {
            CoroutineScope(Dispatchers.IO).launch {
                Stipop.configRepository.postConfigSdk()
            }
        }

        private fun trackViewPicker(){
            Stipop.stickerPickerViewModel?.trackSpv()
        }

        private fun trackViewSearch(){
            StickerSearchView.newInstance().viewModel?.trackViewSearch()
        }

        private fun trackViewStore(){
            CoroutineScope(Dispatchers.IO).launch {
                StipopApi.create().trackViewStore(UserIdBody(Stipop.userId))
            }
        }

        private fun trackViewNew(){
            CoroutineScope(Dispatchers.IO).launch {
                StipopApi.create().trackViewNew(UserIdBody(Stipop.userId))
            }
        }

        private fun trackViewMySticker(){
            CoroutineScope(Dispatchers.IO).launch {
                StipopApi.create().trackViewMySticker(UserIdBody(Stipop.userId))
            }
        }

        private fun trackViewPackage(){
            trackViewPackagePackageId?.let {
                Stipop.packDetailViewModel?.trackViewPackage(it,trackViewPackageEntrancePoint)
            }
            setTrackViewPackageData(null, null)
        }

        internal fun setTrackViewPackageData(trackViewPackagePackageId: Int?, trackViewPackageEntrancePoint: String?){
            this.trackViewPackagePackageId = trackViewPackagePackageId
            this.trackViewPackageEntrancePoint = trackViewPackageEntrancePoint
        }

        private fun postTrackUsingSticker(){
            trackUsingStickerSPSticker?.let{
                when (trackUsingStickerEnum) {
                    TrackUsingStickerEnum.STICKER_PICKER_VIEW_CLASS_SINGLE_TAP -> Stipop.stickerPickerViewClass?.onStickerSingleTap(
                        -1,
                        it
                    )

                    TrackUsingStickerEnum.STICKER_PICKER_VIEW_CLASS_DOUBLE_TAP -> Stipop.stickerPickerViewClass?.onStickerDoubleTap(
                        -1,
                        it
                    )
                    TrackUsingStickerEnum.STICKER_SEARCH_VIEW_SINGLE_TAP -> StickerSearchView.ssvOnStickerTapReRequestDelegate?.ssvOnStickerSingleTapReRequest(-1, it)
                    TrackUsingStickerEnum.STICKER_SEARCH_VIEW_DOUBLE_TAP -> StickerSearchView.ssvOnStickerTapReRequestDelegate?.ssvOnStickerDoubleTapReRequest(-1, it)
                    else -> {}
                }
            }
            setPostTrackUsingStickerData(null, null)
        }

        internal fun setPostTrackUsingStickerData(trackUsingStickerEnum: TrackUsingStickerEnum?, spSticker: SPSticker?){
            this.trackUsingStickerEnum = trackUsingStickerEnum
            this.trackUsingStickerSPSticker = spSticker
        }
    }
}