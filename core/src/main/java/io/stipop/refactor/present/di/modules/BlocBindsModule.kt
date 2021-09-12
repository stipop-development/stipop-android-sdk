package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.data.blocs.StickerKeyboardBlocV1
import io.stipop.refactor.domain.blocs.StickerKeyboardBloc

@Module(
    includes = [
        RepositoryBindsModule::class
    ]
)
interface BlocBindsModule {

    @Binds
    fun bindStickerKeyboardBloc(bloc: StickerKeyboardBlocV1): StickerKeyboardBloc
}
