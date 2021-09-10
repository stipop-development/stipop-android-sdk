package io.stipop.refactor.data.repositories

import android.util.Log
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject


class UserDataRepository @Inject constructor() : UserRepository() {

    override fun setUser(user: SPUser?) {
        Log.d(
            this::class.simpleName, "setUser : \n " +
                    "user -> $user"
        )
        _userChanged.postValue(user)
    }
}
