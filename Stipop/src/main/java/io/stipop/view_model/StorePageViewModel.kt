package io.stipop.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stipop.APIClient
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.model.SPPackage
import io.stipop.model.SPPageMap
import io.stipop.model.SPSection
import io.stipop.model.SectionItemListOrientation
import org.json.JSONObject
import java.io.IOException

class StorePageViewModel : ViewModel(), StorePageViewModelProtocol {

    private var _hasLoadingAllPackageList: Boolean = false
    private var _hasLoadingSearchPackageList: Boolean = false

    private val _storePageMode: MutableLiveData<StorePageMode> = MutableLiveData()

    private val _searchKeyword: MutableLiveData<String> = MutableLiveData()

    private val _storeAllPackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    private val _storeAllPackageListPageMap: MutableLiveData<SPPageMap?> = MutableLiveData()

    private val _storeSectionList: MediatorLiveData<List<SPSection<SPPackage>>> =
        MediatorLiveData<List<SPSection<SPPackage>>>().apply {
            this.addSource(_storeAllPackageList) {
                val trendingSection = SPSection(
                    "Trending",
                    it.filterIndexed { index, _ -> index < 12 },
                    SectionItemListOrientation.HORIZONTAL
                )

                val stickersSection = SPSection(
                    "Stickers",
                    it.filterIndexed { index, _ -> index >= 12 },
                    SectionItemListOrientation.VERTICAL
                )

                this.postValue(
                    listOf(
                        trendingSection,
                        stickersSection
                    )
                )
            }
        }

    private val _storeSearchPackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    private val _storeSearchPackageListPageMap: MutableLiveData<SPPageMap?> = MutableLiveData()

    override val storePageMode: LiveData<StorePageMode>
        get() = _storePageMode
    override val searchKeyword: LiveData<String>
        get() = _searchKeyword
    override val storeAllPackageList: LiveData<List<SPPackage>>
        get() = _storeAllPackageList
    override val storePackageList: LiveData<List<SPSection<SPPackage>>>
        get() = _storeSectionList
    override val storeSearchPackageList: LiveData<List<SPPackage>>
        get() = _storeSearchPackageList

    override fun onChangeStorePageMode(mode: StorePageMode) {
        Log.d(
            this::class.simpleName, "onChangeStorePageMode : " +
                    "mode -> $mode"
        )
        _storePageMode.value.let {
            if (it == null || it != mode) {
                _storePageMode.value = mode
            }
        }
    }

    override fun onChangeSearchKeyword(keyword: String) {
        Log.d(
            this::class.simpleName, "onChangeSearchKeyword : " +
                    "keyword -> $keyword"
        )

        _searchKeyword.value.let {
            if (it == null || it != keyword) {
                _searchKeyword.value = keyword
                _storeSearchPackageListPageMap.value = null
                _storeSearchPackageList.value = listOf()
                onLoadSearchPackageList()
            }
        }
    }

    override fun onLoadAllPackageList() {
        Log.d(this::class.simpleName, "onLoadAllPackageList")
        val currentPageNumber = _storeAllPackageListPageMap.value?.pageNumber ?: 0

        if (_hasLoadingAllPackageList) {
            return
        }
        _hasLoadingAllPackageList = true
        val params = JSONObject()

        params.put("userId", Stipop.userId)
        params.put("pageNumber", currentPageNumber + 1)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)

        APIClient.get(
            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            try {

                if (e != null) {
                    throw e
                }

                response.let {
                    it?.getJSONObject("body").let {
                        it?.getJSONObject("pageMap")?.let {
                            _storeAllPackageListPageMap.postValue(
                                SPPageMap(
                                    it.getInt("pageNumber"),
                                    it.getInt("onePageCountRow"),
                                    it.getInt("totalCount"),
                                    it.getInt("pageCount"),
                                    it.getInt("groupCount"),
                                    it.getInt("groupNumber"),
                                    it.getInt("pageGroupCount"),
                                    it.getInt("startPage"),
                                    it.getInt("endPage"),
                                    it.getInt("startRow"),
                                    it.getInt("endRow"),
                                    it.getInt("modNum"),
                                    it.getInt("listStartNumber"),
                                )
                            )
                        }
                        it?.getJSONArray("packageList")?.let {
                            Log.d(
                                this::class.simpleName,
                                "packageListJson.length() = ${it.length()}"
                            )
                            val result = arrayListOf<SPPackage>()
                            for (i in 0 until it.length()) {
                                val item = it.get(i) as JSONObject
                                result.add(SPPackage(item))
                            }

                            _storeAllPackageList.value.let {
                                if (it == null) {
                                    _storeAllPackageList.postValue(result)
                                } else {
                                    _storeAllPackageList.postValue(it + result)
                                }
                            }
                            _hasLoadingAllPackageList = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }

    override fun onLoadSearchPackageList() {
        Log.d(this::class.simpleName, "onLoadAllPackageList")
        val keyword = _searchKeyword.value ?: ""
        val currentPageNumber = _storeSearchPackageListPageMap.value?.pageNumber ?: 0

        if (_hasLoadingSearchPackageList) {
            return
        }
        _hasLoadingSearchPackageList = true
        val params = JSONObject()

        params.put("userId", Stipop.userId)
        params.put("pageNumber", currentPageNumber + 1)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)
        params.put("q", keyword)

        APIClient.get(
            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            try {

                if (e != null) {
                    throw e
                }

                response.let {
                    it?.getJSONObject("body").let {
                        it?.getJSONObject("pageMap")?.let {
                            _storeSearchPackageListPageMap.postValue(
                                SPPageMap(
                                    it.getInt("pageNumber"),
                                    it.getInt("onePageCountRow"),
                                    it.getInt("totalCount"),
                                    it.getInt("pageCount"),
                                    it.getInt("groupCount"),
                                    it.getInt("groupNumber"),
                                    it.getInt("pageGroupCount"),
                                    it.getInt("startPage"),
                                    it.getInt("endPage"),
                                    it.getInt("startRow"),
                                    it.getInt("endRow"),
                                    it.getInt("modNum"),
                                    it.getInt("listStartNumber"),
                                )
                            )
                        }
                        it?.getJSONArray("packageList")?.let {
                            Log.d(
                                this::class.simpleName,
                                "packageListJson.length() = ${it.length()}"
                            )
                            val result = arrayListOf<SPPackage>()
                            for (i in 0 until it.length()) {
                                val item = it.get(i) as JSONObject
                                result.add(SPPackage(item))
                            }

                            _storeSearchPackageList.value.let {
                                if (it == null) {
                                    _storeSearchPackageList.postValue(result)
                                } else {
                                    _storeSearchPackageList.postValue(it + result)
                                }
                            }
                            _hasLoadingSearchPackageList = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }

    override fun onLoadMoreAllPackageList(lastIndex: Int) {
        Log.d(
            this::class.simpleName, "onLoadMoreAllPackageList : " +
                    "lastIndex -> $lastIndex"
        )

        val _pageMap = _storeAllPackageListPageMap.value ?: return
        val _hasMore = _pageMap.pageNumber < _pageMap.pageCount
        val _limit = _pageMap.onePageCountRow
        val _itemCount = _storeAllPackageList.value?.size ?: return

        if (_hasMore && lastIndex > _itemCount - _limit * 2) {
            onLoadAllPackageList()
        }
    }

    override fun onLoadMoreSearchPackageList(lastIndex: Int) {
        Log.d(
            this::class.simpleName, "onLoadMoreSearchPackageList : " +
                    "lastIndex -> $lastIndex"
        )

        val _pageMap = _storeSearchPackageListPageMap.value ?: return
        val _hasMore = _pageMap.pageNumber < _pageMap.pageCount
        val _limit = _pageMap.onePageCountRow
        val _itemCount = _storeSearchPackageList.value?.size ?: return

        if (_hasMore && lastIndex > _itemCount - _limit * 2) {
            onLoadSearchPackageList()
        }
    }

    override fun onDownload(item: SPPackage) {
        Log.d(this::class.simpleName, "onDownload : " +
                "item.packageId -> ${item.packageId}")

        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("isPurchase", Config.allowPremium)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)

        if (Config.allowPremium == "Y") {
            // 움직이지 않는 스티커
            var price = Config.pngPrice

            if (item.packageAnimated == "Y") {
                // 움직이는 스티커
                price = Config.gifPrice
            }
            params.put("price", price)
        }

        APIClient.post(
            APIClient.APIPath.DOWNLOAD.rawValue + "/${item.packageId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            try {

                e?.let { throw it }

                response?.let {
                    onLoadPackage(item)
                }

            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }

    override fun onLoadPackage(item: SPPackage) {
        Log.d(
            this::class.simpleName, "onLoadPackage : " +
        "item.packageId -> ${item.packageId}")

        val params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(
            APIClient.APIPath.PACKAGE.rawValue + "/${item.packageId}",
            params
        ) { response: JSONObject?, e: IOException? ->
            try {

                e?.let { throw it }

                response?.let {
                    it.getJSONObject("body").getJSONObject("package").let {
                        val _package = SPPackage(it)

                        _storeAllPackageList.value?.let {

                            val index = it.indexOfFirst { value -> value.packageId == item.packageId }

                            Log.d(this::class.simpleName, "index -> $index")

                            val result = arrayListOf<SPPackage>().apply {
                                addAll(it)
                                this[index] = _package
                            }

                            _storeAllPackageList.postValue(result)

                        }

                    }
                }

            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            }
        }

    }
}

interface StorePageViewModelProtocol {
    val storePageMode: LiveData<StorePageMode>
    val searchKeyword: LiveData<String>
    val storeAllPackageList: LiveData<List<SPPackage>>
    val storePackageList: LiveData<List<SPSection<SPPackage>>>
    val storeSearchPackageList: LiveData<List<SPPackage>>

    fun onChangeStorePageMode(mode: StorePageMode)
    fun onChangeSearchKeyword(keyword: String)
    fun onLoadAllPackageList()
    fun onLoadSearchPackageList()
    fun onLoadMoreAllPackageList(lastIndex: Int)
    fun onLoadMoreSearchPackageList(lastIndex: Int)
    fun onDownload(item: SPPackage)
    fun onLoadPackage(item: SPPackage)
}

enum class StorePageMode {
    ALL,
    SEARCH,
}


//////


/*

class StorePageViewModel : ViewModel() {

    private val _storeMode: MutableLiveData<SPStorePageMode> = MutableLiveData()
    val storeMode: LiveData<SPStorePageMode>
        get() = _storeMode

    private val _searchKeyword: MutableLiveData<String> = MutableLiveData()

    private val _searchPageMap: MutableLiveData<SPPageMap?> = MutableLiveData()
    private val _searchPackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    val searchPackageList: LiveData<List<SPPackage>>
        get() = _searchPackageList

    private val _allPageMap: MutableLiveData<SPPageMap?> = MutableLiveData()
    private val _allPackageList: MutableLiveData<List<SPPackage>> = MutableLiveData()
    val allPackageList: LiveData<List<SPPackage>>
        get() = _allPackageList

    fun loadPackageList(keyword: String, pageNumber: Int) {
        Log.d(this::class.simpleName, "loadPackageList : " +
                "keyword -> $keyword ," +
                "pageNumber -> $pageNumber")
        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("pageNumber", pageNumber)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)
        params.put("limit", 12)
        params.put("q", keyword)

        APIClient.get(
            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            try {

                if (e != null) {
                    throw e
                }

                val bodyJson = response?.getJSONObject("body")

                bodyJson?.getJSONObject("pageMap")?.let {
                    if (it != null) {
                        _searchPageMap.postValue(
                            SPPageMap(
                                it.getInt("pageNumber"),
                                it.getInt("onePageCountRow"),
                                it.getInt("totalCount"),
                                it.getInt("pageCount"),
                                it.getInt("groupCount"),
                                it.getInt("groupNumber"),
                                it.getInt("pageGroupCount"),
                                it.getInt("startPage"),
                                it.getInt("endPage"),
                                it.getInt("startRow"),
                                it.getInt("endRow"),
                                it.getInt("modNum"),
                                it.getInt("listStartNumber"),
                            )
                        )
                    }
                }

                bodyJson?.getJSONArray("packageList")?.let {

                    if (it != null) {
                        Log.d(this::class.simpleName,
                            "packageListJson.length() = ${it.length()}")
                        val result = arrayListOf<SPPackage>()
                        for (i in 0 until it.length()) {

                            val item = it.get(i) as JSONObject
                            result.add(SPPackage(item))
                        }

                        _searchPackageList.value.let {
                            if (it != null) {
                                _searchPackageList.postValue(it + result)
                            } else {
                                _searchPackageList.postValue(result)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }

    fun loadMore() {
        _searchKeyword.value?.let { keyword ->
            {
                _searchPageMap.value?.let {
                    loadPackageList(keyword, it.pageNumber + 1)
                }
            }
        }
    }








    fun loadAllPackageList(pageNumber: Int) {
        Log.d(this::class.simpleName, "loadAllPackageList : " +
                "pageNumber -> $pageNumber")
        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("pageNumber", pageNumber)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)
        params.put("limit", 12)

        APIClient.get(
            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            try {

                if (e != null) {
                    throw e
                }

                val bodyJson = response?.getJSONObject("body")

                bodyJson?.getJSONObject("pageMap").let {
                    if (it != null) {
                        _searchPageMap.postValue(
                            SPPageMap(
                                it.getInt("pageNumber"),
                                it.getInt("onePageCountRow"),
                                it.getInt("totalCount"),
                                it.getInt("pageCount"),
                                it.getInt("groupCount"),
                                it.getInt("groupNumber"),
                                it.getInt("pageGroupCount"),
                                it.getInt("startPage"),
                                it.getInt("endPage"),
                                it.getInt("startRow"),
                                it.getInt("endRow"),
                                it.getInt("modNum"),
                                it.getInt("listStartNumber"),
                            )
                        )
                    }
                }

                bodyJson?.getJSONArray("packageList").let {
                    if (it != null) {
                        Log.d(this::class.simpleName,
                            "packageListJson.length() = ${it.length()}")
                        val result = arrayListOf<SPPackage>()
                        for (i in 0 until it.length()) {

                            val item = it.get(i) as JSONObject
                            result.add(SPPackage(item))
                        }

                        _allPackageList.value.let {
                            if (it != null) {
                                _allPackageList.postValue(it + result)
                            } else {
                                _allPackageList.postValue(result)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.message, e)
            }
        }
    }

    fun loadAllPackageListMore() {
        _searchKeyword.value?.let { keyword ->
            {
                _searchPageMap.value?.let {
                    loadPackageList(keyword, it.pageNumber + 1)
                }
            }
        }
    }

    fun searchPackageList(keyword: String) {
        Log.d(this::class.simpleName, "search : " +
                "keyword -> $keyword")
        _searchKeyword.postValue(keyword)
        _searchPageMap.postValue(null)
        _searchPackageList.postValue(listOf())
        loadPackageList(keyword, 1)
    }

    fun onChangeStoreMode(storeMode: SPStorePageMode) {
        _storeMode.value.let {
            if (it == null || it != storeMode) {
                _storeMode.postValue(storeMode)
            }
        }
    }
//
//    fun getKeyword() {
//        APIClient.get(
//            activity as Activity,
//            APIClient.APIPath.SEARCH_KEYWORD.rawValue,
//            null
//        ) { response: JSONObject?, e: IOException? ->
//
//            if (null != response) {
//
//                if (!response.isNull("body")) {
//                    val body = response.getJSONObject("body")
//
//                    if (!body.isNull("keywordList")) {
//                        val keywordList = body.getJSONArray("keywordList")
//
//                        var limit = keywordList.length()
//                        if (limit > 10) {
//                            limit = 10
//                        }
//
//                        for (i in 0 until limit) {
//                            val item = keywordList.get(i) as JSONObject
//
//                            val keyword = Utils.getString(item, "keyword")
//
//                            val tagView = layoutInflater.inflate(R.layout.tag_layout, null, false)
//                            val tagTV = tagView.findViewById<TextView>(R.id.tagTV)
//
//                            val drawable = tagTV.background as GradientDrawable
//                            drawable.setStroke(1, Color.parseColor(Config.themeMainColor))
//
//                            tagTV.setTextColor(Color.parseColor(Config.themeMainColor))
//                            tagTV.text = keyword
//                            tagTV.setOnClickListener {
//
//                                // haptics
//                                val vibrator =
//                                    this._context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                    vibrator.vibrate(
//                                        VibrationEffect.createOneShot(
//                                            100,
//                                            VibrationEffect.DEFAULT_AMPLITUDE
//                                        )
//                                    )
//                                }
//
//                                changeView(false)
//                                inputKeyword = keyword
//                                _binding.keywordET.setText(keyword)
////                                reloadData(false)
//                            }
//
//                            recommendedTagsTL.addView(tagView)
//                        }
//
//                    }
//
//                }
//
//            }
//
//        }
//    }
//

}
*/
