package io.stipop.refactor.present.ui.pages.store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.databinding.FragmentMyActivePackageListBinding
import io.stipop.refactor.present.ui.adapters.MyActivePackageAdapter
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.present.ui.components.common.SPBottomSheetDialog
import io.stipop.refactor.present.ui.listeners.OnHiddenPackageListener
import io.stipop.refactor.present.ui.listeners.OnMovePackageListener
import io.stipop.refactor.present.ui.listeners.OnStartDragListener
import io.stipop.refactor.present.ui.view_models.MyPageViewModel
import javax.inject.Inject

class SPMyActivePackageListFragment : Fragment() {

    private lateinit var _binding: FragmentMyActivePackageListBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop.appComponent.inject(this)

        _binding = FragmentMyActivePackageListBinding.inflate(layoutInflater, container, false).apply {
            activePackageList.layoutManager = LinearLayoutManager(context)
            activePackageList.adapter = MyActivePackageAdapter().apply {

                itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

                onHiddenPackageListener = object : OnHiddenPackageListener {
                    override fun onHidden(item: SPPackage) {
                        val dialog =
                            SPBottomSheetDialog(requireContext())
                        dialog.setOnClickCancelListener {
                            dialog.dismiss()
                        }
                        dialog.setOnClickConfirmLListener {
                            _viewModel.onHiddenPackage(item)
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                }
                onMovePackageListener = object : OnMovePackageListener {
                    override fun onStartMove(item: SPPackage) {
                        Log.d(this@SPMyActivePackageListFragment::class.simpleName, "onStartMove : \n" +
                                "item.id -> ${item.packageId}\n")
                    }
                }
                onStartDragListener = object : OnStartDragListener {
                    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {

                        Log.d(this@SPMyActivePackageListFragment::class.simpleName, "onStartDrag")

                        viewHolder?.let {
                            itemTouchHelper.startDrag(it)
                        }
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
            itemTouchHelper.attachToRecyclerView(activePackageList)
        }

        activity?.let {
            _viewModel.myActivePackageList.observe(it) { value ->
                Log.d(this::class.simpleName, "activePackageList.size -> ${value.size}")
                with(_binding.activePackageList.adapter as? MyActivePackageAdapter) {
                    this?.setItemList(value)
                }
            }
        }
        return _binding.root
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
}
