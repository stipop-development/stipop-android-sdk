package io.stipop.refactor.present.ui.view_model

import io.stipop.refactor.data.repositories.MyStickersRepository
import io.stipop.refactor.data.repositories.UserRepository
import javax.inject.Inject

class KeyboardViewModel  @Inject constructor(
    private val userRepository: UserRepository,
    private val myStickersRepository: MyStickersRepository,
)  {
}
