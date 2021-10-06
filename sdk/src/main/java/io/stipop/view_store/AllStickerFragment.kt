package io.stipop.view_store

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.adapter.*
import io.stipop.api.APIClient
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.custom.TagLayout
import io.stipop.databinding.FragmentAllStickerBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.SPPackage
import io.stipop.view_common.StickerPackageActivity
import io.stipop.viewmodel.AllStickerViewModel
import kotlinx.android.synthetic.main.fragment_all_sticker.*
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder


class AllStickerFragment : BaseFragment() {

    companion object {
        fun newInstance() = Bundle().apply {
        }.let { AllStickerFragment().apply { arguments = it } }
    }

    private var binding: FragmentAllStickerBinding? = null
    private lateinit var viewModel: AllStickerViewModel
    private val allStickerAdapter: AllStickerAdapter by lazy { AllStickerAdapter() }

    ////////////////////////////////////////////////////////////
    var packagePage = 2 // 1 Page -> Trending List
    var totalPage = 2
    lateinit var packageAdapter: PackageAdapter
    var packageData = ArrayList<SPPackage>()

    lateinit var allStickerAdapterOld: AllStickerAdapterOld
    var allStickerData = ArrayList<SPPackage>()

    private var lastItemVisibleFlag = false

    lateinit var packageRV: RecyclerView
    lateinit var trendingLL: LinearLayout

    lateinit var recentKeywordAdapter: RecentKeywordAdapter
    var recentKeywords = ArrayList<String>()

    var popularStickers = ArrayList<SPPackage>()
    lateinit var popularStickerAdapter: PopularStickerAdapter

    lateinit var recommendedTagsTL: TagLayout
    lateinit var popularStickerRV: RecyclerView

    lateinit var noneTV: TextView

    var inputKeyword = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllStickerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(AllStickerViewModel::class.java)

        binding?.allStickerRecyclerView?.adapter = allStickerAdapter
        viewModel.getPackages()
        viewModel.stickerPackages.observeForever { stickers ->
            allStickerAdapter.updateData(stickers)
        }

        PackageDownloadEvent.liveData.observe(viewLifecycleOwner){
            Log.d("STIPOP-DEBUG", "PACKAGE DOWNLOAD EVENT ID : $it")
            // todo DOWNLOAD EVENT
        }

//        clearTextLL.setOnClickListener {
//            keywordET.setText("")
//            inputKeyword = ""
//            Utils.hideKeyboard(requireContext())
//            reloadData(true)
//        }
//
//        keywordET.setOnClickListener {
//            changeView(true)
////            getRecentKeyword()
//        }
//
//        keywordET.setOnFocusChangeListener { view, hasFocus ->
//            if (hasFocus) {
//                changeView(true)
////                getRecentKeyword()
//            }
//        }
//
//        keywordET.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                inputKeyword = Utils.getString(keywordET)
//            }
//        })
//
//        keywordET.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                reloadData(inputKeyword.isEmpty())
//            }
//            false
//        }
//
//
//        packageAdapter = PackageAdapter(packageData, requireContext())
//
//        val mLayoutManager = LinearLayoutManager(requireContext())
//        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
//
//        packageRV.layoutManager = mLayoutManager
//        packageRV.addItemDecoration(RecyclerDecoration(Utils.dpToPx(6F).toInt()))
//        packageRV.adapter = packageAdapter
//
//        packageAdapter.setOnItemClickListener(object : PackageAdapter.OnItemClickListener {
//            override fun onItemClick(position: Int) {
//                if (position > packageData.size) {
//                    return
//                }
//
//                val packageObj = packageData[position]
//
//                goDetail(packageObj.packageId)
//            }
//        })
//
//        if (Config.storeListType == "singular") {
//            // B Type
//            allStickerAdapterOld =
//                AllStickerAdapterOld(requireContext(), R.layout.item_all_sticker_type_b, allStickerData, this)
//        } else {
//            // A Type
//            allStickerAdapterOld =
//                AllStickerAdapterOld(requireContext(), R.layout.item_all_sticker_type_a, allStickerData, this)
//        }
//
//        stickerLV.adapter = allStickerAdapterOld
//        stickerLV.setOnScrollListener(object : AbsListView.OnScrollListener {
//            override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && totalPage > packagePage) {
//                    packagePage += 1
//                    val keyword = Utils.getString(keywordET)
//                    loadPackageData(packagePage, keyword.isNotEmpty())
//                }
//            }
//
//            override fun onScroll(
//                view: AbsListView?,
//                firstVisibleItem: Int,
//                visibleItemCount: Int,
//                totalItemCount: Int
//            ) {
//                lastItemVisibleFlag =
//                    (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
//            }
//
//        })
//        stickerLV.setOnItemClickListener { adapterView, view, i, l ->
//            // position - 1 : addHeaderView 해줬기 때문!
//            val position = i - 1
//            if (position < 0 && position > allStickerData.size) {
//                return@setOnItemClickListener
//            }
//
//            val packageObj = allStickerData[position]
//            goDetail(packageObj.packageId)
//        }
//
//        allStickerAdapterOld.notifyDataSetChanged()
//
//
//        val recentHeaderV = View.inflate(requireContext(), R.layout.header_recent_keyword, null)
//        recentHeaderV.findViewById<TextView>(R.id.keywordClearTV).setOnClickListener {
//            deleteKeyword(null)
//        }
//
////        recentLV.addHeaderView(recentHeaderV)
//
//        val recentFooterV = View.inflate(requireContext(), R.layout.footer_recent_keyword, null)
//        val popularStickerLL = recentFooterV.findViewById<LinearLayout>(R.id.popularStickerLL)
//        val recommendedTagLL = recentFooterV.findViewById<LinearLayout>(R.id.recommendedTagLL)
//        noneTV = recentFooterV.findViewById<TextView>(R.id.emptyTextView)
//
//        recommendedTagsTL = recentFooterV.findViewById(R.id.recommendedTagsTL)
//        popularStickerRV = recentFooterV.findViewById(R.id.popularStickerRV)
//
//        popularStickerAdapter = PopularStickerAdapter(popularStickers, requireContext())
//
//        val mLayoutManager2 = LinearLayoutManager(requireContext())
//        mLayoutManager2.orientation = LinearLayoutManager.HORIZONTAL
//
//        popularStickerRV.layoutManager = mLayoutManager2
//        popularStickerRV.addItemDecoration(RecyclerDecoration(Utils.dpToPx(7F).toInt()))
//        popularStickerRV.adapter = popularStickerAdapter
//
//        popularStickerAdapter.setOnItemClickListener(object :
//            PopularStickerAdapter.OnItemClickListener {
//            override fun onItemClick(position: Int) {
//                if (position > popularStickers.size) {
//                    return
//                }
//
//                val packageObj = popularStickers[position]
//
//                goDetail(packageObj.packageId)
//            }
//        })
//
//        recentLV.addFooterView(recentFooterV)
//
//        recentKeywordAdapter =
//            RecentKeywordAdapter(requireContext(), R.layout.item_recent_keyword, recentKeywords, this)
//        recentLV.adapter = recentKeywordAdapter
//        recentLV.setOnItemClickListener { adapterView, view, i, l ->
//            // position - 1 : addHeaderView 해줬기 때문!
//            val position = i - 1
//            if (position < 0 && position > allStickerData.size) {
//                return@setOnItemClickListener
//            }
//        }
//
//        loadPackageData(1, false)
//
//        loadPackageData(packagePage, false)
//
//        if (Config.storeRecommendedTagShow) {
//            recommendedTagLL.visibility = View.VISIBLE
//            popularStickerLL.visibility = View.GONE
//
//            getKeyword()
//        } else {
//            recommendedTagLL.visibility = View.GONE
//            popularStickerLL.visibility = View.VISIBLE
//
//            getPopularStickers()
//        }

    }

    override fun applyTheme() {
        val drawable = searchBarContainer.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color
        drawable.cornerRadius = Utils.dpToPx(Config.searchbarRadius.toFloat())
        keywordET.setTextColor(Config.getSearchTitleTextColor(requireContext()))
        searchIconIV.setImageResource(Config.getSearchbarResourceId(requireContext()))
        eraseIV.setImageResource(Config.getEraseResourceId(requireContext()))
        searchIconIV.setIconDefaultsColor()
        eraseIV.setIconDefaultsColor()
    }


    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data // Handle the Intent //do stuff here
                if (null != intent) {
                    val packageId = intent.getIntExtra("packageId", -1)
                    if (packageId < 0) {
                        return@registerForActivityResult
                    }

                    for (i in 0 until allStickerData.size) {
                        val item = allStickerData[i]
                        if (item.packageId == packageId) {
                            item.download = "Y"
                            break
                        }
                    }

                    allStickerAdapterOld.notifyDataSetChanged()

                }
            }
        }

    fun goDetail(packageId: Int) {
        val intent = Intent(requireContext(), StickerPackageActivity::class.java)
        intent.putExtra("packageId", packageId)
        // startActivity(intent)
        startForResult.launch(intent)
    }

    fun reloadData(all: Boolean) {
        if (all) {
            loadPackageData(1, false)

            packagePage = 2
        } else {
            packagePage = 1
        }

        totalPage = packagePage
        loadPackageData(packagePage, !all)
    }

    fun loadPackageData(page: Int, search: Boolean) {

        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("pageNumber", page)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)
        params.put("limit", 12)
        params.put("q", Utils.getString(keywordET))

        APIClient.get(
            requireActivity(),
            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->

            if (search) {
                trendingLL.visibility = View.GONE

                packageData.clear()
                packageAdapter.notifyDataSetChanged()

                if (page == 1) {
                    allStickerData.clear()
                    allStickerAdapterOld.notifyDataSetChanged()
                }
            } else {
                trendingLL?.visibility = View.VISIBLE
                if (page == 1) {
                    packageData.clear()
                    packageAdapter.notifyDataSetChanged()
                } else if (page == 2) {
                    allStickerData.clear()
                    allStickerAdapterOld.notifyDataSetChanged()
                }
            }

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        totalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("packageList")) {
                        val packageList = body.getJSONArray("packageList")

                        for (i in 0 until packageList.length()) {
                            val item = packageList.get(i) as JSONObject

                            val spPackage = SPPackage(item)
                            if (page == 1 && !search) {
                                packageData.add(spPackage)
                            } else {
                                allStickerData.add(spPackage)
                            }
                        }

                        if (page == 1 && !search) {
                            packageAdapter.notifyDataSetChanged()
                        } else {
                            allStickerAdapterOld.notifyDataSetChanged()
                        }

                        if (page == 1) {
//                            stickerLV?.smoothScrollToPosition(0)
                        }
                    }

                }

            }

            if (search) {
                if (page == 1) {
                    if (allStickerData.count() > 0) {
                        noneTV.visibility = View.GONE
                        changeView(false)
                        Utils.hideKeyboard(requireContext())
                    } else {
                        noneTV.visibility = View.VISIBLE
                    }
                }
            } else {
                trendingLL.visibility = View.VISIBLE
                if (page == 1) {
                    if (packageData.count() > 0) {
                        noneTV.visibility = View.GONE
                        changeView(false)
                        Utils.hideKeyboard(requireContext())
                    } else {
                        noneTV.visibility = View.VISIBLE
                    }
                } else if (page == 2) {
                    if (allStickerData.count() > 0) {
                        noneTV.visibility = View.GONE
                        changeView(false)
                        Utils.hideKeyboard(requireContext())
                    } else {
                        noneTV.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun getPackInfo(idx: Int, packageId: Int) {

        val params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(
            requireActivity(),
            APIClient.APIPath.PACKAGE.rawValue + "/${packageId}",
            params
        ) { response: JSONObject?, e: IOException? ->
            // println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")
                    val packageObj = body.getJSONObject("package")

                    val spPackage = SPPackage(packageObj)

                    downloadPackage(idx, spPackage)
                }

            } else {
                e?.printStackTrace()
            }
        }

    }

    fun downloadPackage(idx: Int, spPackage: SPPackage) {

        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("isPurchase", Config.allowPremium)
        params.put("lang", Stipop.lang)
        params.put("countryCode", Stipop.countryCode)

        if (Config.allowPremium == "Y") {
            // 움직이지 않는 스티커
            var price = Config.pngPrice

            if (spPackage.packageAnimated == "Y") {
                // 움직이는 스티커
                price = Config.gifPrice
            }
            params.put("price", price)
        }

        APIClient.post(
            requireActivity(),
            APIClient.APIPath.DOWNLOAD.rawValue + "/${spPackage.packageId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {

                val header = response.getJSONObject("header")

                if (Utils.getString(header, "status") == "success") {

                    // download
                    PackUtils.downloadAndSaveLocal(requireActivity(), spPackage) {
                        allStickerAdapterOld.setDownload(idx)
                        Toast.makeText(context, "다운로드 완료!", Toast.LENGTH_LONG).show()
                        allStickerAdapterOld.notifyDataSetChanged()
                    }
                }

            } else {
                e?.printStackTrace()
            }
        }
    }

    fun getRecentKeyword() {

        recentKeywords.clear()
        recentKeywordAdapter.notifyDataSetChanged()

        var params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(
            requireActivity(),
            APIClient.APIPath.SEARCH_RECENT.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->
            // println(response)

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("keywordList")) {
                        val keywordList = body.getJSONArray("keywordList")

                        for (i in 0 until keywordList.length()) {
                            val item = keywordList.get(i) as JSONObject

                            recentKeywords.add(Utils.getString(item, "keyword"))
                        }

                        recentKeywordAdapter.notifyDataSetChanged()
                    }

                }

            }
        }

    }

    fun getKeyword() {
        recommendedTagsTL.removeAllViews()

        APIClient.get(
            requireActivity(),
            APIClient.APIPath.SEARCH_KEYWORD.rawValue,
            null
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {

                // print(response)

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("keywordList")) {
                        val keywordList = body.getJSONArray("keywordList")

                        var limit = keywordList.length()
                        if (limit > 10) {
                            limit = 10
                        }

                        for (i in 0 until limit) {
                            val item = keywordList.get(i) as JSONObject

                            val keyword = Utils.getString(item, "keyword")

                            val tagView = layoutInflater.inflate(R.layout.tag_layout, null, false)
                            val tagTV = tagView.findViewById<TextView>(R.id.tagTV)

                            val drawable = tagTV.background as GradientDrawable
                            drawable.setStroke(1, Color.parseColor(Config.themeMainColor))

                            tagTV.setTextColor(Color.parseColor(Config.themeMainColor))
                            tagTV.text = keyword
                            tagTV.setOnClickListener {

                                // haptics
                                val vibrator =
                                    this.requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(
                                        VibrationEffect.createOneShot(
                                            100,
                                            VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                    )
                                }

                                changeView(false)
                                inputKeyword = keyword
                                keywordET.setText(keyword)
                                reloadData(false)
                            }

                            recommendedTagsTL.addView(tagView)
                        }

                    }

                }

            }

        }
    }

    fun getPopularStickers() {

        popularStickers.clear()

        val params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("limit", 4)

        APIClient.get(
            requireActivity(),
            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("packageList")) {
                        val packageList = body.getJSONArray("packageList")

                        for (i in 0 until packageList.length()) {
                            val item = packageList.get(i) as JSONObject
                            popularStickers.add(SPPackage(item))
                        }

                        popularStickerAdapter.notifyDataSetChanged()

                    }

                }

            }

        }
    }

    fun deleteKeyword(keyword: String?) {

        var path = APIClient.APIPath.SEARCH_RECENT.rawValue + "/${Stipop.userId}"
        if (!keyword.isNullOrEmpty()) {
            val encodeKeyword = URLEncoder.encode(keyword, "UTF-8")
            path += "/$encodeKeyword"
        }

        APIClient.delete(
            requireActivity(),
            path,
            null
        ) { response: JSONObject?, e: IOException? ->

            // println(response)

            if (null != response) {

                if (!response.isNull("header")) {
                    val header = response.getJSONObject("header")

                    if (Utils.getString(header, "status") == "success") {
                        if (!keyword.isNullOrEmpty()) {
                            for (i in 0 until recentKeywords.size) {
                                if (recentKeywords[i] == keyword) {
                                    recentKeywords.removeAt(i)
                                    break
                                }
                            }
                        } else {
                            recentKeywords.clear()
                        }

                        recentKeywordAdapter.notifyDataSetChanged()
                    }

                }

            }

        }
    }

    private fun changeView(search: Boolean) {
//        if (search) {
//            recentLV?.visibility = View.VISIBLE
//            stickerLV?.visibility = View.GONE
//        } else {
//            recentLV?.visibility = View.GONE
//            stickerLV?.visibility = View.VISIBLE
//        }
    }
}