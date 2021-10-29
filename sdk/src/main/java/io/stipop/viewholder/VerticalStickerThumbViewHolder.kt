package io.stipop.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.*
import io.stipop.databinding.ItemVerticalStickerThumbBinding
import io.stipop.models.StickerPackage
import io.stipop.viewholder.delegates.StickerPackageClickDelegate

internal class VerticalStickerThumbViewHolder(private val binding: ItemVerticalStickerThumbBinding, private val delegate: StickerPackageClickDelegate?) :
    RecyclerView.ViewHolder(binding.root) {

    private var stickerPackage: StickerPackage? = null

    init {
        itemView.setOnClickListener {
            stickerPackage?.packageId?.let {
                delegate?.onPackageDetailClicked(it, Constants.Point.STORE)
            }
        }

        with(binding) {
            packageNameTextView.setTextColor(Config.getAllStickerPackageNameTextColor(itemView.context))
            artistNameTextView.setTextColor(Config.getTitleTextColor(itemView.context))

            downloadImageView.setOnClickListener {
                if (stickerPackage?.isDownloaded()==false) {
                    if (Stipop.instance!!.delegate.canDownload(stickerPackage!!.toSPPackage())) {
                        delegate?.onDownloadClicked(bindingAdapterPosition, stickerPackage!!)
                    } else {
                        Utils.alert(itemView.context, "This sticker set can not download now. :(")
                    }
                }
            }
        }

    }

    fun bind(stickerPackage: StickerPackage) {
        this.stickerPackage = stickerPackage
        with(binding) {
            stickerPackageThumb.loadImage(stickerPackage.packageImg)
            packageNameTextView.text = stickerPackage.packageName
            artistNameTextView.text = stickerPackage.artistName

            if (stickerPackage.isDownloaded()) {
                downloadImageView.setImageResource(Config.getCompleteIconResourceId(itemView.context))
                downloadImageView.setIconDefaultsColor()
            } else {
                downloadImageView.setImageResource(Config.getDownloadIconResourceId(itemView.context))
                downloadImageView.setTint()
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, stickerPackageClickDelegate: StickerPackageClickDelegate?): VerticalStickerThumbViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vertical_sticker_thumb, parent, false)
            val binding = ItemVerticalStickerThumbBinding.bind(view)
            return VerticalStickerThumbViewHolder(binding, stickerPackageClickDelegate)
        }
    }
}