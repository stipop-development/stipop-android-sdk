package io.stipop.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.activity.DetailActivity
import io.stipop.adapter.AllStickerAdapter
import io.stipop.adapter.PackageAdapter
import io.stipop.extend.RecyclerDecoration
import io.stipop.model.SPPackage
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_store.*
import kotlinx.android.synthetic.main.fragment_all_sticker.*
import kotlinx.android.synthetic.main.fragment_my_sticker.*
import org.json.JSONObject
import java.io.IOException

class AllStickerFragment : Fragment() {

    lateinit var myContext: Context

    var packagePage = 2 // 1 Page -> Trending List
    var totalPage = 2
    lateinit var packageAdapter: PackageAdapter
    var packageData = ArrayList<SPPackage>()

    lateinit var allStickerAdapter: AllStickerAdapter
    var allStickerData = ArrayList<SPPackage>()

    private var lastItemVisibleFlag = false

    lateinit var packageRV: RecyclerView
    lateinit var trendingLL: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.myContext = container!!.context

        return inflater.inflate(R.layout.fragment_all_sticker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val drawable = searchbarLL.background as GradientDrawable
        drawable.setColor(Color.parseColor(Config.searchbarBgColor)) // solid  color

        searchIconIV.setImageResource(Config.getSearchbarResourceId(myContext))
        eraseIV.setImageResource(Config.getEraseResourceId(myContext))


        val headerV = View.inflate(myContext, R.layout.header_all_sticker, null)

        headerV.findViewById<View>(R.id.underLineV).setBackgroundColor(Config.getUnderLineColor(myContext))
        headerV.findViewById<TextView>(R.id.trendingTV).setTextColor(Config.getTitleTextColor(myContext))
        headerV.findViewById<TextView>(R.id.stickersTV).setTextColor(Config.getTitleTextColor(myContext))


        packageRV = headerV.findViewById(R.id.packageRV)
        trendingLL = headerV.findViewById(R.id.trendingLL)

        stickerLV.addHeaderView(headerV)

        clearTextLL.setOnClickListener {
            keywordET.setText("")
        }

        keywordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val keyword = Utils.getString(keywordET)
                reloadData(keyword.length < 1)
            }
        })

        packageAdapter = PackageAdapter(packageData, myContext)

        val mLayoutManager = LinearLayoutManager(myContext)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        packageRV.layoutManager = mLayoutManager
        packageRV.addItemDecoration(RecyclerDecoration(6))
        packageRV.adapter = packageAdapter

        packageAdapter.setOnItemClickListener(object : PackageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (position > packageData.size) {
                    return
                }

                val packageObj = packageData[position]

                goDetail(packageObj.packageId)
            }
        })

        if (Config.storeListType == "singular") {
            // B Type
            allStickerAdapter =
                AllStickerAdapter(myContext, R.layout.item_all_sticker_type_b, allStickerData)
        } else {
            // A Type
            allStickerAdapter =
                AllStickerAdapter(myContext, R.layout.item_all_sticker_type_a, allStickerData)
        }

        stickerLV.adapter = allStickerAdapter
        stickerLV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && totalPage > packagePage) {
                    packagePage += 1
                    val keyword = Utils.getString(keywordET)
                    loadPackageData(packagePage, keyword.length > 0)
                }
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                lastItemVisibleFlag =
                    (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

        })
        stickerLV.setOnItemClickListener { adapterView, view, i, l ->
            // position - 1 : addHeaderView 해줬기 때문!
            val position = i - 1
            if (position < 0 && position > allStickerData.size) {
                return@setOnItemClickListener
            }

            val packageObj = allStickerData[position]
            goDetail(packageObj.packageId)
        }

        allStickerAdapter.notifyDataSetChanged()

        loadPackageData(1, false)

        loadPackageData(packagePage, false)
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

                    allStickerAdapter.notifyDataSetChanged()

                }
            }
        }

    fun goDetail(packageId: Int) {
        val intent = Intent(myContext, DetailActivity::class.java)
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
            activity as Activity,
            APIClient.APIPath.PACKAGE.rawValue,
            params
        ) { response: JSONObject?, e: IOException? ->

            if (search) {
                trendingLL.visibility = View.GONE

                packageData.clear()
                packageAdapter.notifyDataSetChanged()

                if (page == 1) {
                    allStickerData.clear()
                    allStickerAdapter.notifyDataSetChanged()
                }
            } else {
                trendingLL.visibility = View.VISIBLE
                if (page == 1) {
                    packageData.clear()
                    packageAdapter.notifyDataSetChanged()
                } else if (page == 2) {
                    allStickerData.clear()
                    allStickerAdapter.notifyDataSetChanged()
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
                            allStickerAdapter.notifyDataSetChanged()
                        }

                        if (page == 1) {
                            stickerLV.smoothScrollToPosition(0);
                        }
                    }

                }

            }

        }
    }

    fun downloadPackage(packageId: Int) {

        var params = JSONObject()
        params.put("userId", Stipop.userId)
        params.put("isPurchase", Config.allowPremium)

        APIClient.post(
            activity as Activity,
            APIClient.APIPath.DOWNLOAD.rawValue + "/$packageId",
            params
        ) { response: JSONObject?, e: IOException? ->
            println(response)

            if (null != response) {

                val header = response.getJSONObject("header")

                if (Utils.getString(header, "status") == "success") {
                    Toast.makeText(context, "다운로드 완료!", Toast.LENGTH_LONG).show()

                    downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)
                }

            } else {

            }
        }

    }

}