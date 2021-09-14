package io.stipop.refactor.present.ui.pages.store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.databinding.FragmentMyActivePackageListBinding
import io.stipop.refactor.present.ui.adapters.MyActivePackageAdapter
import io.stipop.refactor.present.ui.view_models.MyPageViewModel
import javax.inject.Inject

class SPMyActivePackageFragment : Fragment() {

    private lateinit var _binding: FragmentMyActivePackageListBinding

    @Inject
    internal lateinit var _viewModel: MyPageViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper

    private var _oldPosition: Int? = null
    private val oldPosition: Int get() = _oldPosition ?: -1

    private var _newPosition: Int? = null
    private val newPosition: Int get() = _newPosition ?: -1

    private val itemTouchHelperCallback: ItemTouchHelper.Callback = object :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)

            when (actionState) {
                ACTION_STATE_DRAG -> {
                    _oldPosition = viewHolder?.bindingAdapterPosition
                }
                ACTION_STATE_IDLE -> {
                    _viewModel.onMoveMyPackageItem(oldPosition, newPosition)
                }
            }
        }

        override fun onMoved(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            fromPos: Int,
            target: RecyclerView.ViewHolder,
            toPos: Int,
            x: Int,
            y: Int
        ) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
            _newPosition = toPos
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            recyclerView.adapter?.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    }

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
            activePackageList.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            Log.d(this::class.simpleName, "activePackageList onScrolled")
                        }
                    })
                }
                adapter = myActivePackageAdapter.apply {
                    itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                    hiddenClick = { item ->

                        _viewModel.onHiddenPackageItem(item)

//                        val dialog =
//                            SPBottomSheetDialog(requireContext())
//                        dialog.setOnClickCancelListener {
//                            dialog.dismiss()
//                        }
//                        dialog.setOnClickConfirmLListener {
//                            _viewModel.onHiddenPackageItem(item)
//                            dialog.dismiss()
//                        }
//                        dialog.show()
                    }
                }

                itemTouchHelper.attachToRecyclerView(activePackageList)
            }
        }

        activity?.let {
            _viewModel.myActivePackageListChanges.observe(it) {
                Log.d(this::class.simpleName, "activePackageList.size -> ${it.size}")
                myActivePackageAdapter.submitList(it)
            }
        }
        return _binding.root
    }

    override fun onStart() {
        super.onStart()
        _viewModel.onLoadMyActivePackageList(-1)
    }
}
