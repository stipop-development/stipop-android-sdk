package io.stipop

import io.stipop.models.SPPackage
import io.stipop.models.SPSticker

interface StipopDelegate {

    /**
     * @author Stipop
     *
     * @param sticker: the sticker item to be single tapped
     *
     * @return if the sticker item was selected or not (not enabled to use)
     */
    fun onStickerSingleTapped(sticker: SPSticker): Boolean


    /**
     * @author Stipop
     *
     * @param sticker: the sticker item to be double tapped
     *
     * @return if the sticker item was selected or not (not enabled to use)
     */
    fun onStickerDoubleTapped(sticker: SPSticker): Boolean


    /**
     * @author Stipop
     *
     * @param spPackage: the sticker package requested to download
     *
     * @return if the sticker package is available or has permission to download
     */
    fun onStickerPackRequested(spPackage: SPPackage): Boolean
}