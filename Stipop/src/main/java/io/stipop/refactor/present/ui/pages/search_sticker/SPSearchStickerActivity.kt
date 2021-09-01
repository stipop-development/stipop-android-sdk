package io.stipop.refactor.present.ui.pages.search_sticker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.stipop.Stipop
import io.stipop.databinding.ActivitySearchStickerBinding
import io.stipop.refactor.present.ui.view_models.SearchStickerViewModelProtocol
import javax.inject.Inject

class SPSearchStickerActivity : AppCompatActivity() {

    lateinit var _binding: ActivitySearchStickerBinding

    @Inject
    lateinit var _viewModel: SearchStickerViewModelProtocol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Stipop.appComponent.inject(this)

        _binding = ActivitySearchStickerBinding.inflate(layoutInflater).apply {
            searchBar.addTextChangedSearchKeywordListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    _viewModel.onChangeSearchKeyword("$p0")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })

        }
        setContentView(_binding.root)


        Log.d(this::class.simpleName, "view model -> $_viewModel")

        _viewModel.apply {
            onLoadSearchKeywordList()
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
