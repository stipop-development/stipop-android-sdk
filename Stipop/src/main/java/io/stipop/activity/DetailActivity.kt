package io.stipop.activity

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.Stipop
import io.stipop.refactor.present.ui.adapters.StoreDetailPackageAdapter
import io.stipop.databinding.ActivityDetailBinding
import io.stipop.refactor.present.ui.components.core.item_decoration.ItemPaddingDecoration
import io.stipop.refactor.present.ui.view_model.DetailViewModel
import javax.inject.Inject


class DetailActivity : AppCompatActivity() {

    companion object {
        const val PACKAGE_ID = "packageId"

        const val REQ_DOWNLOAD_PACKAGE = 0X00
    }

    private lateinit var _binding: ActivityDetailBinding

    @Inject
    internal lateinit var _viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Stipop.appComponent.inject(this)

        _binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(_binding.root)

        _binding.stickerGrid.apply {
            this.layoutManager = GridLayoutManager(context, Config.detailNumOfColumns)
            this.adapter = StoreDetailPackageAdapter()

            val outMetrics = DisplayMetrics()
            val display: Display?
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                display = this.display
                display?.getRealMetrics(outMetrics)
            } else {
                @Suppress("DEPRECATION")
                display = windowManager.defaultDisplay
                @Suppress("DEPRECATION")
                display.getMetrics(outMetrics)
            }

            val paddingValue = (outMetrics.widthPixels / Config.detailNumOfColumns * 0.1).toInt()

            this.addItemDecoration(ItemPaddingDecoration(paddingValue))
            this.clipToPadding = false
        }

        _binding.backButton.setOnClickListener { finish() }
        _binding.closeButton.setOnClickListener { finish() }

        _binding.downloadButton.setOnClickListener {
            _viewModel.selectedPackage.value?.let {
                val _intent = Intent().apply {
                    putExtra(PACKAGE_ID, it.packageId)
                }
                setResult(REQ_DOWNLOAD_PACKAGE, _intent)
            }
        }

        _viewModel.selectedPackage.observe(this) {

            Log.d(this::class.simpleName, "selectedPackage -> $it")

            it?.run {
                Glide.with(_binding.root.context).load(this.packageImg).into(_binding.packageImage)
                _binding.packageName.text = this.packageName
                _binding.artistName.text = this.artistName
                _binding.stickerGrid.adapter.apply {
                    when (this) {
                        is StoreDetailPackageAdapter -> {
                            this.setItemList(it.stickers)
                        }
                    }
                }
                if (this.isDownload) {
                    _binding.downloadButton.text = getString(R.string.downloaded)
                } else {
                    _binding.downloadButton.text = getString(R.string.download)
                }
                _binding.downloadButton.isEnabled = !isDownload

            }
        }

        intent?.getIntExtra(PACKAGE_ID, -1)?.run {
            _viewModel.loadPackage(this)
        }
    }
}
