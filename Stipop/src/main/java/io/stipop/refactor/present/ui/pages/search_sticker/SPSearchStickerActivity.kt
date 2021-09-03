package io.stipop.refactor.present.ui.pages.search_sticker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.databinding.ActivitySearchStickerBinding
import io.stipop.refactor.domain.entities.SPKeywordItem
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.present.ui.adapters.SearchKeywordAdapter
import io.stipop.refactor.present.ui.adapters.SearchStickerAdapter
import io.stipop.refactor.present.ui.components.common.SPPaging
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModelProtocol
import javax.inject.Inject

class SPSearchStickerActivity : AppCompatActivity() {

    lateinit var _binding: ActivitySearchStickerBinding

    @Inject
    lateinit var _viewModel: SearchStickerViewModelProtocol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Stipop.appComponent.inject(this)

        _viewModel.let {

            it.searchKeywordList.observe(this) {
                (_binding.stickerList.adapter as? SPPaging.View<SPKeywordItem>)?.apply {
                    setItemList(it)
                }
            }

            it.searchStickerList.observe(this) {
                (_binding.stickerList.adapter as? SPPaging.View<SPStickerItem>)?.apply {
                    setItemList(it)
                }
            }
        }

        _binding = ActivitySearchStickerBinding.inflate(layoutInflater).apply {
            searchBar.addTextChangedSearchKeywordListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    _viewModel.onChangeSearchKeyword("$p0")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })

            keywordList.layoutManager = LinearLayoutManager(this@SPSearchStickerActivity, RecyclerView.HORIZONTAL, false)
            keywordList.adapter = SearchKeywordAdapter()

            stickerList.layoutManager = GridLayoutManager(this@SPSearchStickerActivity, 3)
            stickerList.adapter = SearchStickerAdapter()

        }
        setContentView(_binding.root)


        Log.d(this::class.simpleName, "view model -> $_viewModel")

    }

    override fun onStart() {
        super.onStart()

        _viewModel.onLoadSearchKeywordList(-1)
        _viewModel.onLoadSearchStickerList("", -1)
    }
}
