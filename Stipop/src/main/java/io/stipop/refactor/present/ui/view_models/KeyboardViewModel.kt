package io.stipop.refactor.present.ui.view_models

import io.stipop.refactor.domain.repositories.MyStickersRepository
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject

class KeyboardViewModel  @Inject constructor(
    private val userRepository: UserRepository,
    private val myStickersRepository: MyStickersRepository,
)  {
}
