package io.stipop

import io.stipop.models.SPPackage
import io.stipop.models.SPSticker

interface StipopDelegate {
    /**
     * Called when 'Stipop Sticker' Clicked or Selected.
     * Return If the selected sticker is (not) enable to use.
     */
    fun onStickerSelected(sticker: SPSticker): Boolean
    /**
     * Called when 'Stipop Sticker Package' Clicked or Selected.
     * Return If the selected sticker package is (not) enable to use.
     */
    fun onStickerPackRequested(spPackage: SPPackage): Boolean
}