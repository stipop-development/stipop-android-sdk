package io.stipop.event

import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.StickerPackage

internal interface MyPackEventDelegate {
    fun onPackageClick(position: Int, stickerPackage: StickerPackage)
    fun onItemClicked(packageId: Int, entrancePoint: String)
    fun onItemLongClicked(position: Int)
    fun onVisibilityClicked(wantToVisible: Boolean, packageId: Int, position: Int)
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
    fun onDragCompleted(fromData: Any, toData: Any)
}