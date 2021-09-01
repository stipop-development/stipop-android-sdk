package io.stipop.refactor.present.ui.pages.search_sticker

import android.os.Bundle
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

        _binding = ActivitySearchStickerBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        Log.d(this::class.simpleName, "view model -> $_viewModel")

        _viewModel.user.observe(this) { user ->
            Log.d(this::class.simpleName, "observe user : $user")
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
