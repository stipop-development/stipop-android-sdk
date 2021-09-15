package io.stipop.view

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.stipop.*
import io.stipop.view.adapter.MyStickerAdapter
import io.stipop.base.BaseFragment
import io.stipop.extend.dragdrop.OnItemHolderEventListener
import io.stipop.extend.dragdrop.SimpleItemTouchHelperCallback
import io.stipop.model.SPPackage
import kotlinx.android.synthetic.main.fragment_my_sticker.*
import org.json.JSONObject
import java.io.IOException

class MyStickerFragment : BaseFragment(), OnItemHolderEventListener {

    companion object {
        fun newInstance() = Bundle().let { MyStickerFragment().apply { arguments = it } }
    }

    lateinit var myStickerAdapter: MyStickerAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    var page = 1
    var totalPage = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_sticker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myStickerAdapter = MyStickerAdapter(this)
        myStickersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myStickersRecyclerView.adapter = myStickerAdapter
        myStickersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = myStickerAdapter.itemCount

                if (lastVisibleItemPosition + 1 == itemTotalCount && totalPage > page) {
                    // 리스트 마지막 도착! 다음 페이지 로드!
                    page += 1
                    if (stickerTypeTV.tag == 1) {
                        loadMySticker()
                    } else {
                        loadMyHiddenSticker()
                    }
                }
            }
        })

        myStickerAdapter.setOnRecyclerAdapterEventListener(this)
        val callback = SimpleItemTouchHelperCallback(myStickerAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(myStickersRecyclerView)

        stickerTypeTV.setOnClickListener {
            stickerTypeTV.tag = if (stickerTypeTV.tag == 2) {
                1
            } else {
                2
            }

            reloadData()
        }

        loadMySticker()
    }

    override fun refreshTheme() {
        stickerTypeTV.setTextColor(Config.getActiveHiddenStickerTextColor(requireContext()))
        stickerTypeTV.setBackgroundColor(Config.getHiddenStickerBackgroundColor(requireContext()))
    }

    fun reloadData() {
        page = 1
        totalPage = 1

        if (stickerTypeTV.tag == 1) {
            stickerTypeTV.text = getString(R.string.view_hidden_stickers)
            stickerTypeTV.setBackgroundColor(Config.getHiddenStickerBackgroundColor(requireContext()))

            loadMySticker()
        } else {
            stickerTypeTV.text = getString(R.string.view_active_stickers)
            stickerTypeTV.setBackgroundColor(Config.getActiveStickerBackgroundColor(requireContext()))

            loadMyHiddenSticker()
        }
    }

    fun loadMySticker() {
        val params = JSONObject()
        params.put("pageNumber", page)
        params.put("limit", 20)

        APIClient.get(
            activity as Activity,
            APIClient.APIPath.MY_STICKER.rawValue + "/${Stipop.userId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (page == 1) {
                myStickerAdapter.clearData()
            }

            if (null != response) {
                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        totalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("packageList")) {
                        val packageList = body.getJSONArray("packageList")
                        val arrayList = ArrayList<SPPackage>()
                        for (i in 0 until packageList.length()) {
                            arrayList.add(SPPackage(packageList.get(i) as JSONObject))
                        }
                        arrayList.sortByDescending { data -> data.order }
                        arrayList.forEach{
                            Log.d("mySticker", "${it.packageId}, ${it.order}")
                        }
                        myStickerAdapter.updateData(arrayList)
                    }
                }
            }

            setNoResultView()
        }
    }

    private fun setNoResultView() {
        if (myStickerAdapter.itemCount > 0) {
            listLL.visibility = View.VISIBLE
            noneTV.visibility = View.GONE
        } else {
            listLL.visibility = View.GONE
            noneTV.visibility = View.VISIBLE
        }
    }

    fun loadMyHiddenSticker() {
        val params = JSONObject()
        params.put("pageNumber", page)
        params.put("limit", 20)

        APIClient.get(
            activity as Activity,
            APIClient.APIPath.MY_STICKER_HIDE.rawValue + "/${Stipop.userId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (page == 1) {
                myStickerAdapter.clearData()
            }

            if (null != response) {

                val header = response.getJSONObject("header")

                if (!response.isNull("body") && Utils.getString(header, "status") == "success") {
                    val body = response.getJSONObject("body")

                    if (!body.isNull("pageMap")) {
                        val pageMap = body.getJSONObject("pageMap")
                        totalPage = Utils.getInt(pageMap, "pageCount")
                    }

                    if (!body.isNull("packageList")) {
                        val packageList = body.getJSONArray("packageList")
                        val arrayList = ArrayList<SPPackage>()
                        for (i in 0 until packageList.length()) {
                            arrayList.add(SPPackage(packageList.get(i) as JSONObject))
                        }
                        myStickerAdapter.updateData(arrayList)
                    }
                }
            } else {
                e?.printStackTrace()
            }

            setNoResultView()
        }
    }

    fun myStickerOrder(fromPosition: SPPackage, toPosition: SPPackage) {

        val fromOrder = fromPosition.order
        val toOrder = toPosition.order

        val params = JSONObject()
        params.put("currentOrder", fromOrder)
        params.put("newOrder", toOrder)

        APIClient.put(
            activity as Activity,
            APIClient.APIPath.MY_STICKER_ORDER.rawValue + "/${Stipop.userId}",
            params
        ) { response: JSONObject?, e: IOException? ->
            Log.d("myStickerOrder", "$response")
            if (null != response) {
                val header = response.getJSONObject("header")
                val status = Utils.getString(header, "status")
                if (status == "fail") {
                    Toast.makeText(requireContext(), "ERROR!!", Toast.LENGTH_LONG).show()
                } else {
                    if (!response.isNull("body")) {
                        val body = response.getJSONObject("body")
                        if (!body.isNull("packageList")) {
                            val packageList = body.getJSONArray("packageList")
                            for (i in 0 until packageList.length()) {
                                val resPackage = SPPackage(packageList[i] as JSONObject)
                                for (j in 0 until myStickerAdapter.itemCount) {
                                    if (resPackage.packageId == myStickerAdapter.getData()[j].packageId) {
                                        myStickerAdapter.getData()[j].order = resPackage.order
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun hidePackage(packageId: Int, position: Int) {
        val params = JSONObject()
        APIClient.put(
            activity as Activity,
            APIClient.APIPath.MY_STICKER_HIDE.rawValue + "/${Stipop.userId}/$packageId",
            params
        ) { response: JSONObject?, e: IOException? ->
            if (null != response) {
                val header = response.getJSONObject("header")
                val status = Utils.getString(header, "status")

                if (status == "success") {
                    myStickerAdapter.onItemRemove(position)
                    setNoResultView()
                } else {
                    Toast.makeText(requireContext(), "ERROR!!", Toast.LENGTH_LONG).show()
                }
            }


            // update keyboard package
            val broadcastIntent = Intent()
            broadcastIntent.action =
                "${requireContext().packageName}.RELOAD_PACKAGE_LIST_NOTIFICATION"
            requireContext().sendBroadcast(broadcastIntent)
        }

    }

    fun showConfirmAlert(packageId: Int, position: Int) {
        val customSelectProfilePicBottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)

        val layoutBottomSheetView = this.layoutInflater.inflate(R.layout.bottom_alert, null)

        val drawable =
            layoutBottomSheetView.findViewById<LinearLayout>(R.id.containerLL).background as GradientDrawable
        drawable.setColor(Config.getAlertBackgroundColor(requireContext())) // solid  color

        layoutBottomSheetView.findViewById<TextView>(R.id.titleTV)
            .setTextColor(Config.getAlertTitleTextColor(requireContext()))
        layoutBottomSheetView.findViewById<TextView>(R.id.contentsTV)
            .setTextColor(Config.getAlertContentsTextColor(requireContext()))

        val cancelTV = layoutBottomSheetView.findViewById<TextView>(R.id.cancelTV)
        val hideTV = layoutBottomSheetView.findViewById<TextView>(R.id.hideTV)

        cancelTV.setTextColor(Config.getAlertButtonTextColor(requireContext()))
        hideTV.setTextColor(Config.getAlertButtonTextColor(requireContext()))

        cancelTV.setOnClickListener {
            customSelectProfilePicBottomSheetDialog.dismiss()
        }

        hideTV.setOnClickListener {
            hidePackage(packageId, position)

            customSelectProfilePicBottomSheetDialog.dismiss()
        }

        customSelectProfilePicBottomSheetDialog.setContentView(layoutBottomSheetView)
        customSelectProfilePicBottomSheetDialog.show()
    }

    override fun onItemClicked(position: Int) {
    }

    override fun onItemLongClicked(position: Int) {
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

}