package io.stipop.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.adapter.AllStickerAdapter
import io.stipop.adapter.PackageAdapter
import io.stipop.extend.RecyclerDecoration
import io.stipop.model.SPPackage
import kotlinx.android.synthetic.main.fragment_all_sticker.*
import org.json.JSONObject
import java.io.IOException

class AllStickerFragment: Fragment() {

    lateinit var myContext: Context

    var packagePage = 2 // 1 Page -> Trending List
    lateinit var packageAdapter: PackageAdapter
    var packageData = ArrayList<SPPackage>()

    lateinit var allStickerAdapter: AllStickerAdapter
    var allStickerData = ArrayList<SPPackage>()

    private var lastItemVisibleFlag = false

    lateinit var packageRV: RecyclerView

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

        val header = View.inflate(myContext, R.layout.header_all_sticker, null)
        packageRV = header.findViewById(R.id.packageRV)

        stickerLV.addHeaderView(header)

        clearTextLL.setOnClickListener {
            keywordET.setText("")
        }

        keywordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                reloadData()
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

                val item = packageData[position]

            }
        })

        if (Config.allStickerType == "B") {
            // B Type
            allStickerAdapter = AllStickerAdapter(myContext, R.layout.item_all_sticker_type_b, allStickerData)
        } else {
            // A Type
            allStickerAdapter = AllStickerAdapter(myContext, R.layout.item_all_sticker_type_a, allStickerData)
        }

        stickerLV.adapter = allStickerAdapter
        stickerLV.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    packagePage += 1
                    loadPackageData(packagePage)
                }
            }

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

        })

        allStickerAdapter.notifyDataSetChanged()

        loadPackageData(1)

        loadPackageData(packagePage)
    }

    fun reloadData() {
        loadPackageData(1)

        packagePage = 2
        loadPackageData(packagePage)
    }

    fun loadPackageData(page: Int) {

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

            if (page == 1) {
                packageData.clear()
                packageAdapter.notifyDataSetChanged()
            } else if (page == 2) {
                allStickerData.clear()
                allStickerAdapter.notifyDataSetChanged()
            }

            if (null != response) {

                if (!response.isNull("body")) {
                    val body = response.getJSONObject("body")

                    val packageList = body.getJSONArray("packageList")

                    for (i in 0 until packageList.length()) {
                        val item = packageList.get(i) as JSONObject

                        val spPackage = SPPackage(item)
                        if (page == 1) {
                            packageData.add(spPackage)
                        } else {
                            allStickerData.add(spPackage)
                        }
                    }

                    if (page == 1) {
                        packageAdapter.notifyDataSetChanged()
                    } else {
                        allStickerAdapter.notifyDataSetChanged()
                    }

                }
            }

        }
    }
}