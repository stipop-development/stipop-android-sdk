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
import io.stipop.refactor.domain.entities.SPPackageItem
import io.stipop.refactor.present.ui.components.common.SPBottomSheetDialog
import io.stipop.refactor.present.ui.listeners.OnHiddenPackageListener
import io.stipop.refactor.present.ui.listeners.OnMovePackageListener
import io.stipop.refactor.present.ui.listeners.OnStartDragListener
import io.stipop.refactor.present.ui.view_models.MyPageViewModel
import javax.inject.Inject

class SPMyActivePackageFragment : Fragment() {

    private lateinit var _binding: FragmentMyActivePackageListBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper

    private lateinit var myActivePackageAdapter: MyActivePackageAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop._appComponent.inject(this)

        myActivePackageAdapter = MyActivePackageAdapter()

        _binding = FragmentMyActivePackageListBinding.inflate(layoutInflater, container, false).apply {
            activePackageList.layoutManager = LinearLayoutManager(context)
            activePackageList.adapter = myActivePackageAdapter.apply {

                itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

                onHiddenPackageListener = object : OnHiddenPackageListener {
                    override fun onHidden(item: SPPackageItem) {
                        val dialog =
                            SPBottomSheetDialog(requireContext())
                        dialog.setOnClickCancelListener {
                            dialog.dismiss()
                        }
                        dialog.setOnClickConfirmLListener {
                            _viewModel.onHiddenPackageItem(item)
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                }
                onMovePackageListener = object : OnMovePackageListener {
                    override fun onStartMove(item: SPPackage) {
                        Log.d(this@SPMyActivePackageFragment::class.simpleName, "onStartMove : \n" +
                                "item.id -> ${item.packageId}\n")
                    }
                }
                onStartDragListener = object : OnStartDragListener {
                    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {

                        Log.d(this@SPMyActivePackageFragment::class.simpleName, "onStartDrag")

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
//                    _viewModel.onLoadMyActivePackageList()
                }
            })
            itemTouchHelper.attachToRecyclerView(activePackageList)
        }

        activity?.let {
            _viewModel.myActivePackageListChanges.observe(it) {
                Log.d(this::class.simpleName, "activePackageList.size -> ${it.size}")
                myActivePackageAdapter.submitList(it)
            }
        }
        return _binding.root
    }
}
