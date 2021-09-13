package io.stipop.refactor.present.ui.pages.store

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.Stipop
import io.stipop.databinding.ActivityDetailBinding
import io.stipop.refactor.present.ui.adapters.StoreDetailPackageAdapter
import io.stipop.refactor.present.ui.components.core.item_decoration.ItemPaddingDecoration
import io.stipop.refactor.present.ui.view_models.DetailViewModel
import javax.inject.Inject


class SPDetailActivity : AppCompatActivity() {

    companion object {
        const val PACKAGE_ID = "packageId"

        const val REQ_DOWNLOAD_PACKAGE = 0X00
    }

    private lateinit var _binding: ActivityDetailBinding

    @Inject
    internal lateinit var _viewModel: DetailViewModel

    private lateinit var storeDetailPackageAdapter: StoreDetailPackageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Stipop._appComponent.inject(this)

        intent?.getIntExtra(PACKAGE_ID, -1)?.let {
            packageId ->

        storeDetailPackageAdapter = StoreDetailPackageAdapter()

        _binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(_binding.root)

        _binding.stickerGrid.apply {
            this.layoutManager = GridLayoutManager(context, Config.detailNumOfColumns)
            this.adapter = storeDetailPackageAdapter

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
            _viewModel.onDownloadPackageItem(packageId)
//            _viewModel.packageItemChanges.value?.let {
//                val _intent = Intent().apply {
//                    putExtra(PACKAGE_ID, it.packageId)
//                }
//                setResult(REQ_DOWNLOAD_PACKAGE, _intent)
//            }
        }

        _viewModel.packageItemChanges.observe(this) {

            Log.d(this::class.simpleName, "selectedPackage -> $it")

            it?.run {
                Glide.with(_binding.root.context).load(this.packageImg).into(_binding.packageImage)
                _binding.packageName.text = this.packageName
                _binding.artistName.text = this.artistName
                _binding.downloadButton.isEnabled = !(this.isDownload == "Y")

                if (_binding.downloadButton.isEnabled) {
                    _binding.downloadButton.text = getString(R.string.download)
                } else {
                    _binding.downloadButton.text = getString(R.string.downloaded)
                }

                storeDetailPackageAdapter.submitList(it.stickers)

            }
        }

            _viewModel.onLoadPackage(packageId)
        }
    }
}
