package io.stipop.adapter.viewholder

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.R
import io.stipop.StipopUtils
import io.stipop.adapter.StickerDefaultAdapter
import io.stipop.databinding.ItemStickerThumbBinding
import io.stipop.models.SPSticker
import kotlinx.android.synthetic.main.item_sticker_thumb.view.*

internal class StickerThumbViewHolder(
    private val binding: ItemStickerThumbBinding,
    val delegate: StickerDefaultAdapter.OnStickerClickListener? = null,
    private val isLockable: Boolean
) :
    RecyclerView.ViewHolder(binding.root) {

    private var spSticker: SPSticker? = null

    init {
        when(Config.stickerDoubleTap){
            true -> doubleTapSetup()
            false -> singleTapSetup()
        }
    }
    private fun singleTapSetup(){
        itemView.setOnClickListener {
            spSticker?.let {
                val isLocked = itemView.lockImageView.visibility == View.VISIBLE
                delegate?.onStickerSingleTap(absoluteAdapterPosition, it, isLocked)
            }
        }
    }
    private fun doubleTapSetup(){
        var i: Int = 0
        var singleCount: Int = 0
        var doubleCount: Int = 0
        var doubleTap: Boolean = false
        var singleTap: Boolean = false

        itemView.setOnClickListener {
            i += 1
            if (i == 2) {
                doubleTap = true
                singleTap = false
                i = 0
            } else if (i == 1) {
                doubleTap = false
                singleTap = true
            } else {
                doubleTap = false
                singleTap = false
                i = 0
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (singleTap) {
                    if (singleCount % 3 == 0) {
                        spSticker?.let {
                            val isLocked = itemView.lockImageView.visibility == View.VISIBLE
                            delegate?.onStickerSingleTap(absoluteAdapterPosition, it, isLocked)
                        }
                    } else {
                        singleCount += 1
                    }
                } else if (doubleTap) {
                    if (doubleCount == 1) {
                        spSticker?.let {
                            val isLocked = itemView.lockImageView.visibility == View.VISIBLE
                            delegate?.onStickerDoubleTap(absoluteAdapterPosition, it, isLocked)
                        }
                        doubleCount = 0
                    } else {
                        doubleCount += 1
                    }
                }
                i = 0
            }, 200)
        }
    }

    fun bind(sticker: SPSticker) {
        spSticker = sticker
        val isDownload = sticker.isDownload == "Y"
        val priceTier = sticker.getPriceTier()
        val isLocked = StipopUtils.isLocked(isLockable = this.isLockable,
            isDownload = isDownload,
            priceTier = priceTier)
        setLock(isLocked)
        binding.imageView.loadImage(sticker.stickerImgLocalFilePath ?: sticker.stickerImg, false)
    }

    private fun setLock(isLocked: Boolean) {
        when(isLocked) {
            true -> {
                binding.lockImageView.visibility = View.VISIBLE

                binding.imageView.alpha = 0.4F
            }
            false -> {
                binding.lockImageView.visibility = View.GONE

                binding.imageView.alpha = 1.0F
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            delegate: StickerDefaultAdapter.OnStickerClickListener? = null,
            isLockable: Boolean
        ): StickerThumbViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sticker_thumb, parent, false)
            val binding = ItemStickerThumbBinding.bind(view)
            return StickerThumbViewHolder(binding, delegate, isLockable)
        }
    }
}