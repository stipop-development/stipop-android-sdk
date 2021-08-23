package io.stipop.fragment

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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.activity.DetailActivity
import io.stipop.adapter.store.storePage.DownloadPackageCallback
import io.stipop.adapter.store.storePage.SearchPackageAdapter
import io.stipop.adapter.store.storePage.SelectPackageCallback
import io.stipop.adapter.store.storePage.StoreAllPackageAdapter
import io.stipop.databinding.FragmentStorePageBinding
import io.stipop.model.SPPackage
import io.stipop.viewModel.StorePageMode
import io.stipop.viewModel.StorePageViewModel


class StorePageFragment : Fragment() {
    private lateinit var _binding: FragmentStorePageBinding
    private lateinit var _viewModel: StorePageViewModel
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                Log.d(
                    this::class.simpleName, "registerForActivityResult : " +
                            "result.resultCode -> ${result.resultCode}"
                )

                when (result.resultCode) {
                    DetailActivity.REQ_DOWNLOAD_PACKAGE -> {

                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(this::class.simpleName, "onCreateView")
        _binding = FragmentStorePageBinding.inflate(layoutInflater, container, false)
        _viewModel = activityViewModels<StorePageViewModel>().value

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(this::class.simpleName, "onViewCreated")
        _binding.storeAllPackageList.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = StoreAllPackageAdapter().apply {
                this.selectPackageCallback = object : SelectPackageCallback {
                    override fun onSelect(item: SPPackage) {
                        resultLauncher?.launch(Intent(activity, DetailActivity::class.java).apply {
                            this.putExtra(DetailActivity.PACKAGE_ID, item.packageId)
                        })
                    }
                }
                this.downloadPackageCallback = object : DownloadPackageCallback {
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
        _binding.eraseIV.apply {
            this.setOnClickListener {
                _viewModel.onChangeStorePageMode(StorePageMode.ALL)
            }
        }
        _binding.keywordET.apply {
            this.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    if (hasFocus) {
                        _viewModel.onChangeStorePageMode(StorePageMode.SEARCH)
                    }
                }
            })
            this.addTextChangedListener(object : TextWatcher {
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

                _binding.keywordET.clearFocus()
                val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(_binding.root.windowToken, 0)
            }
            StorePageMode.SEARCH -> {
                _binding.storeAllPackageList.visibility = View.GONE
                _binding.searchPackageList.visibility = View.VISIBLE
            }
        }
        _binding.keywordET.setText("")
    }
}
