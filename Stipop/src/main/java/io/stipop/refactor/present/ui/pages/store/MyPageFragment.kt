package io.stipop.refactor.present.ui.pages.store

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.refactor.present.ui.adapters.MyActivePackageAdapter
import io.stipop.databinding.FragmentMyPageBinding
import io.stipop.extend.dragdrop.OnRecyclerAdapterEventListener
import io.stipop.refactor.data.models.SPMyPageMode
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.present.ui.adapters.MyHiddenPackageAdapter
import io.stipop.refactor.present.ui.listeners.OnActivePackageListener
import io.stipop.refactor.present.ui.listeners.OnHiddenPackageListener
import io.stipop.refactor.present.ui.view_models.MyPageViewModel
import javax.inject.Inject

class MyPageFragment : Fragment(), OnRecyclerAdapterEventListener {

    private lateinit var _binding: FragmentMyPageBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper

    var data = ArrayList<SPPackage>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop.appComponent.inject(this)

        _binding = FragmentMyPageBinding.inflate(layoutInflater, container, false).apply {
            activePackageList.layoutManager = LinearLayoutManager(context)
            activePackageList.adapter = MyActivePackageAdapter().apply {
                onHiddenPackageListener = object : OnHiddenPackageListener {
                    override fun onHidden(item: SPPackage) {
                        _viewModel.onHiddenPackage(item)
                    }
                }
            }
            activePackageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.d(this::class.simpleName, "activePackageList onScrolled")
                    _viewModel.onLoadMyActivePackageList()
                }
            })

            hiddenPackageList.layoutManager = LinearLayoutManager(context)
            hiddenPackageList.adapter = MyHiddenPackageAdapter().apply {
                onActivePackageListener = object : OnActivePackageListener {
                    override fun onActive(item: SPPackage) {
                        _viewModel.onActivePackage(item)
                    }
                }
            }
            hiddenPackageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.d(this::class.simpleName, "hiddenPackageList onScrolled")
                    _viewModel.onLoadMyHiddenPackageList()
                }
            })

            myPackageToggleButton.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> {
                        _viewModel.onChangeMyPackageMode(SPMyPageMode.HIDDEN)
                    }
                    false -> {
                        _viewModel.onChangeMyPackageMode(SPMyPageMode.ACTIVE)
                    }
                }
            }
        }

        activity?.let {
            _viewModel.myPageMode.observe(it) { value ->
                Log.d(this::class.simpleName, "myPageMode -> $value")

                when (value) {
                    SPMyPageMode.ACTIVE -> {
                        _viewModel.onLoadMyActivePackageList()
                    }
                    SPMyPageMode.HIDDEN -> {
                        _viewModel.onLoadMyHiddenPackageList()
                    }
                }

                _onChangeMyPageMode(value)
            }

            _viewModel.activePackageList.observe(it) { value ->
                Log.d(this::class.simpleName, "activePackageList.size -> ${value.size}")
                with(_binding.activePackageList.adapter as? MyActivePackageAdapter) {
                    this?.setItemList(value)
                }
            }

            _viewModel.hiddenPackageList.observe(it) { value ->
                Log.d(this::class.simpleName, "hiddenPackageList.size -> ${value.size}")
                with(_binding.hiddenPackageList.adapter as? MyHiddenPackageAdapter) {
                    this?.setItemList(value)
                }
            }

        }
        return _binding.root
    }

    private fun _onChangeMyPageMode(mode: SPMyPageMode?) {
        when (mode) {
            SPMyPageMode.ACTIVE -> {
                _binding.activePackageList.visibility = VISIBLE
                _binding.hiddenPackageList.visibility = GONE
            }
            SPMyPageMode.HIDDEN -> {
                _binding.activePackageList.visibility = GONE
                _binding.hiddenPackageList.visibility = VISIBLE
            }
        }
    }

    fun myStickerOrder(fromPosition: Int, toPosition: Int) {
        /*

        // TODO refactor

        Log.d(this::class.simpleName, "myStickerOrder : " +
                "fromPosition -> $fromPosition , " +
                "toPosition -> $toPosition" +
                "")

        val fromPackageObj = data[fromPosition]
        val toPackageObj = data[toPosition]

        val fromOrder = fromPackageObj.order
        val toOrder = toPackageObj.order

        val params = JSONObject()
        params.put("currentOrder", fromOrder)
        params.put("newOrder", toOrder)

        APIClient.put(
            activity as Activity,
            APIClient.APIPath.MY_STICKER_ORDER.rawValue + "/${Stipop.userId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            if (null != response) {

                val header = response.getJSONObject("header")
                val status = Utils.getString(header, "status")

                if (status == "fail") {
                    Log.e(this::class.simpleName, e?.message, e)
                    Toast.makeText(view?.context, "ERROR!!", Toast.LENGTH_LONG).show()
                    data.sortBy { data -> data.order }

                } else {

                    // order 변경
                    if (!response.isNull("body")) {
                        val body = response.getJSONObject("body")
                        if (!body.isNull("packageList")) {
                            val packageList = body.getJSONArray("packageList")

                            for (i in 0 until packageList.length()) {
                                val resPackage = SPPackage(packageList[i] as JSONObject)
                                for (j in 0 until data.size) {
                                    if (resPackage.packageId == data[j].packageId) {
                                        data[j].order = resPackage.order
                                        break
                                    }
                                }
                            }
                        }
                    }

                }

            }

        }
        */

    }

    fun showConfirmAlert(packageId: Int, position: Int) {
        Log.d(this::class.simpleName, "showConfirmAlert")
//        val customSelectProfilePicBottomSheetDialog =
//            BottomSheetDialog(root.context, R.style.CustomBottomSheetDialogTheme)

        val layoutBottomSheetView = this.layoutInflater.inflate(R.layout.bottom_alert, null)

        val drawable =
            layoutBottomSheetView.findViewById<LinearLayout>(R.id.containerLL).background as GradientDrawable
//        drawable.setColor(Config.getAlertBackgroundColor(root.context)) // solid  color

//        layoutBottomSheetView.findViewById<TextView>(R.id.titleTV)
//            .setTextColor(Config.getAlertTitleTextColor(root.context))
//        layoutBottomSheetView.findViewById<TextView>(R.id.contentsTV)
//            .setTextColor(Config.getAlertContentsTextColor(root.context))

        val cancelTV = layoutBottomSheetView.findViewById<TextView>(R.id.cancelTV)
        val hideTV = layoutBottomSheetView.findViewById<TextView>(R.id.hideTV)

//        cancelTV.setTextColor(Config.getAlertButtonTextColor(root.context))
//        hideTV.setTextColor(Config.getAlertButtonTextColor(root.context))

//        cancelTV.setOnClickListener {
//            customSelectProfilePicBottomSheetDialog.dismiss()
//        }
//
//        hideTV.setOnClickListener {
//            hidePackage(packageId, position)
//
//            customSelectProfilePicBottomSheetDialog.dismiss()
//        }
//
//        customSelectProfilePicBottomSheetDialog.setContentView(layoutBottomSheetView)
//        customSelectProfilePicBottomSheetDialog.show()
    }

    override fun onItemClicked(position: Int) {
        Log.d(this::class.simpleName, "onItemClicked")
    }

    override fun onItemLongClicked(position: Int) {
        Log.d(this::class.simpleName, "onItemLongClicked")
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        Log.d(this::class.simpleName, "onDragStarted")
        itemTouchHelper.startDrag(viewHolder)
    }

}
