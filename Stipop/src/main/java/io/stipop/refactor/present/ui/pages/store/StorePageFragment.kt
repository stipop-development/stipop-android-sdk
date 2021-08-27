package io.stipop.refactor.present.ui.pages.store

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.activity.DetailActivity
import io.stipop.databinding.FragmentStorePageBinding
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.present.ui.adapters.SearchPackageAdapter
import io.stipop.refactor.present.ui.adapters.StoreAllPackageAdapter
import io.stipop.refactor.present.ui.listeners.OnClickPackageListener
import io.stipop.refactor.present.ui.listeners.OnDownloadPackageListener
import io.stipop.refactor.present.ui.view_model.StorePageMode
import io.stipop.refactor.present.ui.view_model.StorePageViewModel
import javax.inject.Inject


class StorePageFragment : Fragment() {
    private lateinit var _binding: FragmentStorePageBinding

    @Inject
    internal lateinit var _viewModel: StorePageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(this::class.simpleName, "onCreateView")
        Stipop.appComponent.inject(this)

        _binding = FragmentStorePageBinding.inflate(layoutInflater, container, false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(this::class.simpleName, "onViewCreated")
        _binding.storeAllPackageList.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = StoreAllPackageAdapter().apply {
                this.onClickPackageListener = object : OnClickPackageListener {
                    override fun onClick(item: SPPackage) {
                        startActivity(Intent(activity, DetailActivity::class.java).apply {
                            this.putExtra(DetailActivity.PACKAGE_ID, item.packageId)
                        })
                    }
                }
                this.onDownloadPackageListener = object : OnDownloadPackageListener {
                    override fun onDownload(item: SPPackage) {
                        _viewModel.onDownload(item)
                    }
                }
            }
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        _binding.keywordET.clearFocus()
                        val inputMethodManager =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        _viewModel.onLoadMoreAllPackageList((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
                    }
                }
            })
        }
        _binding.searchPackageList.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = SearchPackageAdapter()
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        _binding.keywordET.clearFocus()
                        val inputMethodManager =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        _viewModel.onLoadMoreSearchPackageList((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
                    }
                }
            })
        }

        _binding.searchBar.apply {
            setOnClickSearchDeleteButtonListener {
                _viewModel.onChangeStorePageMode(StorePageMode.ALL)
            }
            setOnFocusChangeSearchKeywordListener { v, hasFocus ->
                if (hasFocus) {
                    _viewModel.onChangeStorePageMode(StorePageMode.SEARCH)
                }
            }
            addTextChangedSearchKeywordListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    _viewModel.onChangeSearchKeyword("$p0")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }

        activity?.let {
            _viewModel.storePageMode.observe(it) { value ->
                Log.d(this::class.simpleName, "storeMode -> $value")
                _onChangeStorePageMode(value)
            }

            _viewModel.storeAllPackageList.observe(it) { value ->
                Log.d(this::class.simpleName, "storePackageSectionList.size -> ${value.size}")
                with(_binding.storeAllPackageList.adapter as? StoreAllPackageAdapter) {
                    this?.setItemList(value)
                }
            }

            _viewModel.storeSearchPackageList.observe(it) { value ->
                Log.d(this::class.simpleName, "searchPackageList.size -> ${value.size}")
                with(_binding.searchPackageList.adapter as? SearchPackageAdapter) {
                    this?.setItemList(value)
                }
            }

            _viewModel.onLoadAllPackageList()
        }
    }

    private fun _onChangeStorePageMode(mode: StorePageMode) {
        Log.d(this::class.simpleName, "onChangeStoreMode")
        when (mode) {
            StorePageMode.ALL -> {
                _binding.storeAllPackageList.visibility = View.VISIBLE
                _binding.searchPackageList.visibility = View.GONE
            }
            StorePageMode.SEARCH -> {
                _binding.storeAllPackageList.visibility = View.GONE
                _binding.searchPackageList.visibility = View.VISIBLE
            }
        }
        _binding.keywordET.setText("")
    }
}
