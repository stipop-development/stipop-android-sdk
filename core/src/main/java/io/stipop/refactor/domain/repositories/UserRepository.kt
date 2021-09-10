package io.stipop.refactor.domain.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.domain.entities.SPUser

abstract class UserRepository {
    protected val _userChanged: MutableLiveData<SPUser> = MutableLiveData()
    val userChanges: LiveData<SPUser> = _userChanged
    val currentUser: SPUser? get() = userChanges.value

    abstract fun setUser(user: SPUser?)
}

