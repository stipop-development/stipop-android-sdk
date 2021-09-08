package io.stipop.refactor.present.ui.pages.store

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.databinding.FragmentStoreSearchPackageListBinding
import io.stipop.refactor.present.ui.adapters.SearchPackageAdapter
import io.stipop.refactor.present.ui.view_models.StorePageViewModel
import javax.inject.Inject


class StoreSearchPackageListFragment : Fragment() {
    private lateinit var _binding: FragmentStoreSearchPackageListBinding

    @Inject
    internal lateinit var _viewModel: StorePageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop.appComponent.inject(this)

        _binding = FragmentStoreSearchPackageListBinding.inflate(layoutInflater, container, false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(this::class.simpleName, "onViewCreated")

        _binding.storeSearchPackageList.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = SearchPackageAdapter()
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        val inputMethodManager =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        _viewModel.onLoadStoreSearchPackageList(lastIndex = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
                    }
                }
            })
        }

        activity?.let {
            _viewModel.storeSearchPackageList.observe(it) { value ->
                Log.d(this::class.simpleName, "storeSearchPackageList.size -> ${value.size}")
                with(_binding.storeSearchPackageList.adapter as? SearchPackageAdapter) {
                    this?.setItemList(value)
                }
            }

            _viewModel.onLoadStoreSearchPackageList("")
        }
    }
}
