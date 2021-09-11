package io.stipop.refactor.present.ui.pages.search_sticker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Stipop
import io.stipop.databinding.ActivitySearchStickerBinding
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.present.ui.adapters.SearchKeywordAdapter
import io.stipop.refactor.present.ui.adapters.SearchStickerAdapter
import io.stipop.refactor.present.ui.listeners.OnItemSelectListener
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModel
import javax.inject.Inject

class SPSearchStickerActivity : AppCompatActivity() {

    lateinit var _binding: ActivitySearchStickerBinding

    @Inject
    lateinit var _viewModel: SearchStickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Stipop._appComponent.inject(this)

        _viewModel.let {

            it.keywordList.observe(this) {

                Log.e(this::class.simpleName, "it -> $it")

                (_binding.keywordList.adapter as? SearchKeywordAdapter)?.apply {
                    itemList = it
                    notifyDataSetChanged()
                }
            }

            it.stickerList.observe(this) {
                (_binding.stickerList.adapter as? SearchStickerAdapter)?.apply {
                    itemList = it
                    notifyDataSetChanged()
                }
            }
        }

        _binding = ActivitySearchStickerBinding.inflate(layoutInflater).apply {

            searchBar.let {
                it.addTextChangedSearchKeywordListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun afterTextChanged(p0: Editable?) {
                        _viewModel.onChangeKeyword("$p0")
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                })
            }

            keywordList.apply {
                layoutManager =
                    LinearLayoutManager(this@SPSearchStickerActivity, RecyclerView.HORIZONTAL, false).apply {
                        addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                                super.onScrollStateChanged(recyclerView, newState)
                                when (newState) {
                                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                                        _viewModel.onLoadKeywordList(findLastCompletelyVisibleItemPosition())
                                    }
                                }
                            }
                        })
                    }
                adapter = SearchKeywordAdapter().apply {
                    onItemSelectListener = object : OnItemSelectListener<SPKeywordItem> {
                        override fun onSelect(item: SPKeywordItem) {
                            onChangeKeyword(item.keyword)
                        }
                    }
                }
            }

            stickerList.apply {
                layoutManager = GridLayoutManager(this@SPSearchStickerActivity, Config.detailNumOfColumns).apply {
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            when (newState) {
                                RecyclerView.SCROLL_STATE_DRAGGING -> {
                                    _viewModel.onLoadStickerList(findLastCompletelyVisibleItemPosition())
                                }
                            }
                        }
                    })
                }
                adapter = SearchStickerAdapter()
            }
        }
        setContentView(_binding.root)

        Log.d(this::class.simpleName, "view model -> $_viewModel")
    }

    private fun onChangeKeyword(keyword: String) {
        _viewModel.onChangeKeyword(keyword)
    }

    override fun onStart() {
        super.onStart()

        _viewModel.onLoadKeywordList(-1)
        _viewModel.onLoadStickerList(-1)
    }
}
