package io.stipop.refactor.data.blocs

import androidx.lifecycle.LiveData
import io.stipop.refactor.domain.blocs.StickerSendBloc
import io.stipop.refactor.domain.entities.SPStickerItem
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.domain.services.StickerSendService
import io.stipop.refactor.domain.services.StickerStoreService
import javax.inject.Inject

class StickerSendBlocV1
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val stickerSendService: StickerSendService,
        private val stickerStoreService: StickerStoreService
    )
    : StickerSendBloc() {
    override val stickerChanges: LiveData<SPStickerItem>
        get() = TODO("Not yet implemented")

    override fun onSelectStickerItem(id: Int) {
        
    }
}
